package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.ResourceDto;
import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.service.ResourceAppService;
import cn.xiaolin.core.service.VideoSourceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 播放源写入与读回。
 *
 * <ul>
 *   <li>{@link ResourceDto#getId()} 对应 {@code video_source.video_id}；</li>
 *   <li>{@code mp4 / m3u8 / mpd} 分别落成 {@code type=mp4 / hls / dash}；</li>
 *   <li>按 {@code videoId + type + src} 去重，重复回写不产生新行。</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceAppServiceImpl implements ResourceAppService {

    private static final String TYPE_MP4 = "mp4";
    private static final String TYPE_HLS = "hls";
    private static final String TYPE_DASH = "dash";
    private static final int DEFAULT_RESOLUTION = 1080;

    private final VideoSourceService videoSourceService;

    @Override
    public ResourceDto getResourceById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("resource id 不能为空");
        }
        List<VideoSource> sources = videoSourceService.list(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, id));

        String mp4 = null;
        String m3u8 = null;
        String mpd = null;
        for (VideoSource source : sources) {
            String type = source.getType() == null ? "" : source.getType().toLowerCase();
            switch (type) {
                case TYPE_MP4 -> mp4 = source.getSrc();
                case TYPE_HLS -> m3u8 = source.getSrc();
                case TYPE_DASH -> mpd = source.getSrc();
                default -> log.debug("忽略未知播放源类型：{}", source.getType());
            }
        }

        return ResourceDto.builder()
                .id(id)
                .mp4(mp4)
                .m3u8(m3u8)
                .mpd(mpd)
                .build();
    }

    @Override
    public void saveResource(ResourceDto resourceDto) {
        if (resourceDto == null) {
            throw new IllegalArgumentException("resource 不能为空");
        }
        Long videoId = resourceDto.getId();
        if (videoId == null) {
            throw new IllegalArgumentException("resource 的 videoId 不能为空");
        }

        saveIfAbsent(videoId, TYPE_MP4, resourceDto.getMp4());
        saveIfAbsent(videoId, TYPE_HLS, resourceDto.getM3u8());
        saveIfAbsent(videoId, TYPE_DASH, resourceDto.getMpd());

        log.info("播放源已保存：videoId={}, mp4={}, m3u8={}, mpd={}",
                videoId, resourceDto.getMp4(), resourceDto.getM3u8(), resourceDto.getMpd());
    }

    /**
     * 已存在同 (videoId, type, src) 时跳过，保证回写幂等。空白地址不落库。
     */
    private void saveIfAbsent(Long videoId, String type, String src) {
        if (src == null || src.isBlank()) {
            return;
        }
        boolean exists = videoSourceService.exists(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, videoId)
                .eq(VideoSource::getType, type)
                .eq(VideoSource::getSrc, src));
        if (exists) {
            log.debug("播放源已存在，跳过：videoId={}, type={}", videoId, type);
            return;
        }

        VideoSource source = new VideoSource();
        source.setVideoId(videoId);
        source.setSrc(src);
        source.setType(type);
        source.setResolution(DEFAULT_RESOLUTION);
        videoSourceService.save(source);
    }
}
