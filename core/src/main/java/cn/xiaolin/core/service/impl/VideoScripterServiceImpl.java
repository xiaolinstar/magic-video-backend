package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoScripterReqDto;
import cn.xiaolin.core.service.VideoScripterService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoScripter;
import cn.xiaolin.core.domain.mapper.VideoScripterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_scripter(视频编剧)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoScripterServiceImpl extends ServiceImpl<VideoScripterMapper, VideoScripter>
    implements VideoScripterService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<VideoScripter> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoScripter> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<VideoScripter> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<VideoScripter> updateAndReturn(VideoScripterReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoScripter> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getScripterId()), VideoScripter::getScripterId, dto.getScripterId())
                .set(Objects.nonNull(dto.getVideoId()), VideoScripter::getVideoId, dto.getVideoId())
                .eq(VideoScripter::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<VideoScripter> saveAndReturn(VideoScripterReqDto dto) {
        VideoScripter videoScripter = VideoScripter.builder()
                .id(dto.getId())
                .scripterId(dto.getScripterId())
                .videoId(dto.getVideoId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoScripter);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.ofNullable(videoScripter) : Optional.empty();
    }
}




