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
 *   <li>{@code src} 只保存稳定播放地址：去掉预签名查询串，长度不超过 DDL {@code VARCHAR(2048)}。</li>
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

    /** 与 {@code video_source.src VARCHAR(2048)} 对齐，超长直接拒绝，避免截断后无法播放。 */
    static final int MAX_PLAYBACK_SRC_LENGTH = 2048;

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
                videoId,
                stablePlaybackSrc(resourceDto.getMp4()),
                stablePlaybackSrc(resourceDto.getM3u8()),
                stablePlaybackSrc(resourceDto.getMpd()));
    }

    /**
     * 已存在同 (videoId, type, src) 时跳过，保证回写幂等。空白地址不落库。
     * 预签名 URL 只保留问号前的公开对象路径。
     */
    private void saveIfAbsent(Long videoId, String type, String src) {
        String stable = stablePlaybackSrc(src);
        if (stable == null) {
            return;
        }
        boolean exists = videoSourceService.exists(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, videoId)
                .eq(VideoSource::getType, type)
                .eq(VideoSource::getSrc, stable));
        if (exists) {
            log.debug("播放源已存在，跳过：videoId={}, type={}", videoId, type);
            return;
        }

        VideoSource source = new VideoSource();
        source.setVideoId(videoId);
        source.setSrc(stable);
        source.setType(type);
        source.setResolution(DEFAULT_RESOLUTION);
        videoSourceService.save(source);
    }

    /**
     * 长期播放地址不带过期签名。{@code ?} / {@code #} 之后全部丢弃。
     *
     * @return 可落库的地址；空白输入返回 {@code null}
     */
    static String stablePlaybackSrc(String src) {
        if (src == null || src.isBlank()) {
            return null;
        }
        String trimmed = src.trim();
        int end = trimmed.length();
        int query = trimmed.indexOf('?');
        int fragment = trimmed.indexOf('#');
        if (query >= 0 && query < end) {
            end = query;
        }
        if (fragment >= 0 && fragment < end) {
            end = fragment;
        }
        String stable = trimmed.substring(0, end);
        if (stable.isBlank()) {
            return null;
        }
        if (stable.length() > MAX_PLAYBACK_SRC_LENGTH) {
            throw new IllegalArgumentException(
                    "播放地址超过 video_source.src 上限 " + MAX_PLAYBACK_SRC_LENGTH);
        }
        return stable;
    }
}
