package cn.xiaolin.utils.config;

import cn.xiaolin.utils.utility.ContextUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description MybatisPlus 自动填充插件
 * @create 2023/7/8
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * 插入操作自动填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createdByUserId", ContextUtil.getUserId());
        metaObject.setValue("updatedByUserId", ContextUtil.getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动更新[update]...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updatedByUserId", ContextUtil.getUserId());
    }
}
