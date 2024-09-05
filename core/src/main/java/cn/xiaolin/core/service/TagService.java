package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.TagReqDto;
import cn.xiaolin.core.domain.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【tag(标签)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface TagService extends IService<Tag> {
    Optional<Tag> findItemById(Long id);

    Optional<Tag> deleteAndReturnById(Long id);

    Optional<Tag> updateAndReturn(TagReqDto dto);

    Optional<Tag> saveAndReturn(TagReqDto dto);
}
