package cn.xiaolin.message.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转码任务消息：视频上传到对象存储后，由 multimedia 投递到消息队列，has 消费并转码。
 *
 * <p><b>为什么必须是这个类型而不是 {@code java.net.URL}</b>：
 * 消息转换器的 Jackson 可信包白名单只放行 {@code cn.xiaolin.message.entity} 与
 * {@code cn.xiaolin.message.dto}。发送 {@code java.net.URL} 对象时，生产端会写入
 * {@code __TypeId__: java.net.URL}，消费端反序列化会抛出
 * {@code IllegalArgumentException: The class 'java.net.URL' is not in the trusted packages}，
 * 导致消息永远无法被消费。因此消息体必须是本包下的普通 DTO。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2026/11/7
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TranscodeMessage {

    /** 源视频访问地址（mp4），供 ffmpeg 拉取 */
    private String sourceUrl;

    /**
     * 关联的影视 id，对应 {@code video_source.video_id}。
     * 转码完成后由 has 回写 core，落库时作为播放源归属的影视。
     */
    private Long videoId;

    /**
     * 资源标识（源视频对象名，去掉后缀）。
     * 同时作为转码幂等键：同一资源重复投递时不会重复转码、不会覆盖已有产物。
     */
    private String resourceKey;
}
