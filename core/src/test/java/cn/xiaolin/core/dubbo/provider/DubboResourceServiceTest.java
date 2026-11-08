package cn.xiaolin.core.dubbo.provider;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.service.VideoSourceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 转码结果落库通道测试。
 *
 * <p>验证 {@link DubboResourceService}：
 * <ol>
 *   <li>mp4 / m3u8 / mpd 三个地址分别落成三行 {@code video_source}；</li>
 *   <li>重复回写不会产生重复行（幂等）；</li>
 *   <li>{@code getResourceById} 能按 type 把三行还原回 DTO。</li>
 * </ol>
 *
 * <p>需要 dev 环境的 MySQL / Redis / Nacos 可用。
 */
@SpringBootTest
@ActiveProfiles("dev")
class DubboResourceServiceTest {

    /** 测试用影视 id */
    private static final Long TEST_VIDEO_ID = 90001L;

    @Autowired
    private DubboResourceService dubboResourceService;

    @Autowired
    private VideoSourceService videoSourceService;

    @Test
    void saveResourceShouldWriteThreeRowsAndBeIdempotent() {
        // 清理上一轮遗留，保证断言口径干净
        videoSourceService.remove(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, TEST_VIDEO_ID));

        ResourceDto dto = ResourceDto.builder()
                .id(TEST_VIDEO_ID)
                .md5("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .mp4("http://minio/magic-video/src.mp4")
                .m3u8("http://minio/magic-hls/src.m3u8")
                .mpd("http://minio/magic-dash/src.mpd")
                .build();

        // 第一次回写
        dubboResourceService.saveResource(dto);

        List<VideoSource> rows = videoSourceService.list(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, TEST_VIDEO_ID));
        assertEquals(3, rows.size(), "mp4/m3u8/mpd 应落成三行 video_source");
        assertTrue(rows.stream().anyMatch(r -> "mp4".equals(r.getType())));
        assertTrue(rows.stream().anyMatch(r -> "hls".equals(r.getType())));
        assertTrue(rows.stream().anyMatch(r -> "dash".equals(r.getType())));

        // 第二次回写（模拟消息重投）：不应产生重复行
        dubboResourceService.saveResource(dto);
        List<VideoSource> rowsAgain = videoSourceService.list(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, TEST_VIDEO_ID));
        assertEquals(3, rowsAgain.size(), "重复回写必须幂等，不得产生重复行");

        // 读回：按 type 还原
        ResourceDto loaded = dubboResourceService.getResourceById(TEST_VIDEO_ID);
        assertNotNull(loaded);
        assertEquals("http://minio/magic-video/src.mp4", loaded.getMp4());
        assertEquals("http://minio/magic-hls/src.m3u8", loaded.getM3u8());
        assertEquals("http://minio/magic-dash/src.mpd", loaded.getMpd());

        // 清理
        videoSourceService.remove(new LambdaQueryWrapper<VideoSource>()
                .eq(VideoSource::getVideoId, TEST_VIDEO_ID));
    }
}
