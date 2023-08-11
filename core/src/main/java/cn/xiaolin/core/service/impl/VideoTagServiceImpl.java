package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoTagReqDto;
import cn.xiaolin.core.domain.entity.VideoActor;
import cn.xiaolin.core.service.VideoTagService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoTag;
import cn.xiaolin.core.domain.mapper.VideoTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_tag(视频标签关联关系表)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoTagServiceImpl extends ServiceImpl<VideoTagMapper, VideoTag>
    implements VideoTagService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<VideoTag> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoTag> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<VideoTag> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<VideoTag> updateAndReturn(VideoTagReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoTag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getTagId()), VideoTag::getTagId, dto.getId())
                .set(Objects.nonNull(dto.getVideoId()), VideoTag::getVideoId, dto.getVideoId())
                .eq(Objects.nonNull(dto.getId()), VideoTag::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<VideoTag> saveAndReturn(VideoTagReqDto dto) {
        VideoTag videoTag = VideoTag.builder()
                .id(dto.getId())
                .tagId(dto.getTagId())
                .videoId(dto.getVideoId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoTag);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(videoTag) : Optional.empty();
    }
}




