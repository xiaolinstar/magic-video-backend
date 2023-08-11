package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoDirectorReqDto;
import cn.xiaolin.core.domain.dto.VideoScripterReqDto;
import cn.xiaolin.core.domain.entity.VideoScripter;
import cn.xiaolin.core.service.VideoDirectorService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoDirector;
import cn.xiaolin.core.domain.mapper.VideoDirectorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_director(视频导演)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoDirectorServiceImpl extends ServiceImpl<VideoDirectorMapper, VideoDirector>
    implements VideoDirectorService {
    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<VideoDirector> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoDirector> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<VideoDirector> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<VideoDirector> updateAndReturn(VideoDirectorReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoDirector> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getDirectorId()), VideoDirector::getDirectorId, dto.getDirectorId())
                .set(Objects.nonNull(dto.getVideoId()), VideoDirector::getVideoId, dto.getVideoId())
                .eq(VideoDirector::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<VideoDirector> saveAndReturn(VideoDirectorReqDto dto) {
        VideoDirector videoDirector = VideoDirector.builder()
                .id(dto.getId())
                .directorId(dto.getDirectorId())
                .videoId(dto.getVideoId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoDirector);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.ofNullable(videoDirector) : Optional.empty();
    }
}




