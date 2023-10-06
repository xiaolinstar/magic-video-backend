package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoActorReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoActor;
import cn.xiaolin.core.service.VideoActorService;
import cn.xiaolin.core.domain.mapper.VideoActorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_actor(视频演员)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoActorServiceImpl extends ServiceImpl<VideoActorMapper, VideoActor>
    implements VideoActorService{

    @Override
    public Optional<VideoActor> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoActor> deleteAndReturnById(Long id) {
        Optional<VideoActor> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<VideoActor> updateAndReturn(VideoActorReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoActor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getActorId()), VideoActor::getActorId, dto.getActorId())
                .set(Objects.nonNull(dto.getVideoId()), VideoActor::getVideoId, dto.getVideoId())
                .eq(Objects.nonNull(dto.getId()), VideoActor::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<VideoActor> saveAndReturn(VideoActorReqDto dto) {
        VideoActor videoActor = VideoActor.builder()
                .id(dto.getId())
                .videoId(dto.getVideoId())
                .actorId(dto.getActorId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoActor);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(videoActor) : Optional.empty();
    }
}




