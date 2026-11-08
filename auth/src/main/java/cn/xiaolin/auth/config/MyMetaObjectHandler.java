package cn.xiaolin.auth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 插入/更新时填充审计字段。原 {@code ContextUtil} 没有任何写入方，用户 id 保持缺省 0。
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createdByUserId", SYSTEM_USER_ID);
        metaObject.setValue("updatedByUserId", SYSTEM_USER_ID);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动更新[update]...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updatedByUserId", SYSTEM_USER_ID);
    }
}
