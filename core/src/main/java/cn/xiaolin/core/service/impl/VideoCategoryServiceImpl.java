package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoCategoryReqDto;
import cn.xiaolin.core.domain.entity.VideoActor;
import cn.xiaolin.core.service.VideoCategoryService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoCategory;
import cn.xiaolin.core.domain.mapper.VideoCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_category(视频分类关联关系表)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory>
    implements VideoCategoryService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<VideoCategory> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoCategory> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<VideoCategory> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<VideoCategory> updateAndReturn(VideoCategoryReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoCategory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getCategoryId()), VideoCategory::getCategoryId, dto.getCategoryId())
                .set(Objects.nonNull(dto.getVideoId()), VideoCategory::getVideoId, dto.getVideoId())
                .eq(Objects.nonNull(dto.getId()), VideoCategory::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<VideoCategory> saveAndReturn(VideoCategoryReqDto dto) {
        VideoCategory videoCategory = VideoCategory.builder()
                .id(dto.getId())
                .videoId(dto.getVideoId())
                .categoryId(dto.getCategoryId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoCategory);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(videoCategory) : Optional.empty();
    }
}




