package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.TagReqDto;
import cn.xiaolin.core.service.TagService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Tag;
import cn.xiaolin.core.domain.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【tag(标签)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

    @Override
    public Optional<Tag> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Tag> deleteAndReturnById(Long id) {
        Optional<Tag> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<Tag> updateAndReturn(TagReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), Tag::getName, dto.getName())
                .eq(Tag::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()): Optional.empty();
    }

    @Override
    public Optional<Tag> saveAndReturn(TagReqDto dto) {
        Tag tag = Tag.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        boolean saved = false;
        try {
            saved = this.save(tag);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(tag) : Optional.empty();
    }
}




