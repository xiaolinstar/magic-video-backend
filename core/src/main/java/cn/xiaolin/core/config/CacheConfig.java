package cn.xiaolin.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * core 服务的 Spring Cache（Redis 实现）配置。
 *
 * <p>说明：
 * <ul>
 *   <li>TTL 以分钟为单位（历史版本误写成 {@code Duration.ofMillis(10L)} / {@code Duration.ofMillis(30L)}，
 *       导致缓存几乎立即失效，等同于关闭缓存）。</li>
 *   <li>缓存值不使用 {@link java.util.Optional} 包装：{@code Optional} 会被序列化器拆包，
 *       读回时得到的是被包裹的对象而不是 {@code Optional}，会导致缓存命中时类型转换失败。
 *       需要缓存“可能不存在”的对象时，直接缓存具体对象并返回 {@code null}，
 *       由 {@code disableCachingNullValues} 保证空值不写入缓存。</li>
 *   <li>Jackson 多态类型校验仅放行本项目类型与缓存实际用到的 JDK 集合/时间/数值类型
 *       （{@code java.math.BigDecimal} 非 final，同样会写入类型信息），
 *       不设置 {@code allowIfBaseType(Object.class)}——该配置会先匹配所有基类，
 *       使子类型白名单完全失效，等同于不校验。</li>
 * </ul>
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description SpringCache配置类
 * @create 2023/9/2
 */
@Configuration
public class CacheConfig {

    /** 全局 key 前缀，便于按服务隔离和批量清理 */
    private static final String REDIS_CACHE_PREFIX = "magic-video::";

    /** 默认缓存过期时间：10 分钟 */
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    /** 允许参与 Jackson 多态反序列化的类型前缀 */
    private static final String TRUSTED_PROJECT_PREFIX = "cn.xiaolin.";
    private static final String TRUSTED_JDK_UTIL_PREFIX = "java.util.";
    private static final String TRUSTED_JDK_TIME_PREFIX = "java.time.";
    private static final String TRUSTED_JDK_MATH_PREFIX = "java.math.";

    @Bean
    public CacheManager coreCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(DEFAULT_TTL)
                .prefixCacheNameWith(REDIS_CACHE_PREFIX)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(buildRedisValueSerializer()));
        return RedisCacheManager.builder()
                .cacheWriter(redisCacheWriter)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /**
     * 构建缓存值序列化器：支持 Java 8 时间类型，且多态类型只允许白名单内的包。
     */
    private GenericJackson2JsonRedisSerializer buildRedisValueSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 缓存值读回时忽略未知字段：VO 中可能存在只读派生属性（如 VideoVO#getEpisodeNumber），
        // 序列化时会写出但无法回填；忽略它们可避免缓存命中直接报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(TRUSTED_PROJECT_PREFIX)
                        .allowIfSubType(TRUSTED_JDK_UTIL_PREFIX)
                        .allowIfSubType(TRUSTED_JDK_TIME_PREFIX)
                        .allowIfSubType(TRUSTED_JDK_MATH_PREFIX)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
