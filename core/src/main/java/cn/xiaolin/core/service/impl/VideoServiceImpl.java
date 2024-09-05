package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video(视频)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService {

    @Override
    public Optional<Video> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Video> deleteAndReturnById(Long id) {
        Optional<Video> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<Video> updateAndReturn(VideoReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<Video> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), Video::getName, dto.getName())
                .set(Objects.nonNull(dto.getIcon()), Video::getIcon, dto.getIcon())
                .set(Objects.nonNull(dto.getRating()), Video::getRating, dto.getRating())
                .set(Objects.nonNull(dto.getAlias()), Video::getAlias, dto.getAlias())
                .set(Objects.nonNull(dto.getDescription()), Video::getDescription, dto.getDescription())
                .set(Objects.nonNull(dto.getYear()), Video::getYear, dto.getYear())
                .set(Objects.nonNull(dto.getReleaseDate()), Video::getReleaseDate, dto.getReleaseDate())
                .set(Objects.nonNull(dto.getResourceId()), Video::getResourceId, dto.getResourceId())
                .eq(Video::getId, dto.getId());

        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<Video> saveAndReturn(VideoReqDto dto) {
        Video video = Video.builder()
                .id(dto.getId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .description(dto.getDescription())
                .year(dto.getYear())
                .releaseDate(dto.getReleaseDate())
                .rating(dto.getRating())
                .resourceId(dto.getResourceId())
                .alias(dto.getAlias())
                .build();
        boolean saved = false;
        try {
            saved = this.save(video);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.ofNullable(video) : Optional.empty();
    }
}




