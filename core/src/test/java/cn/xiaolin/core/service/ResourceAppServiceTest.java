package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.ResourceDto;
import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.service.impl.ResourceAppServiceImpl;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceAppServiceTest {

    @Mock
    private VideoSourceService videoSourceService;

    @InjectMocks
    private ResourceAppServiceImpl service;

    @BeforeAll
    static void initLambdaCache() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), VideoSource.class);
    }

    @Test
    void saveResource_writesThreeTypes_idempotent() {
        when(videoSourceService.exists(any())).thenReturn(false);
        service.saveResource(ResourceDto.builder().id(1L).mp4("a").m3u8("b").mpd("c").build());

        ArgumentCaptor<VideoSource> captor = ArgumentCaptor.forClass(VideoSource.class);
        verify(videoSourceService, times(3)).save(captor.capture());
        List<VideoSource> saved = captor.getAllValues();
        assertEquals(1L, saved.get(0).getVideoId());
        assertTrue(saved.stream().anyMatch(s -> "mp4".equals(s.getType()) && "a".equals(s.getSrc())));
        assertTrue(saved.stream().anyMatch(s -> "hls".equals(s.getType()) && "b".equals(s.getSrc())));
        assertTrue(saved.stream().anyMatch(s -> "dash".equals(s.getType()) && "c".equals(s.getSrc())));
        assertTrue(saved.stream().allMatch(s -> Integer.valueOf(1080).equals(s.getResolution())));

        when(videoSourceService.exists(any())).thenReturn(true);
        service.saveResource(ResourceDto.builder().id(1L).mp4("a").m3u8("b").mpd("c").build());
        verify(videoSourceService, times(3)).save(any());
    }

    @Test
    void saveResource_skipsBlankSources() {
        when(videoSourceService.exists(any())).thenReturn(false);
        service.saveResource(ResourceDto.builder().id(2L).mp4("only-mp4").m3u8("  ").mpd(null).build());

        ArgumentCaptor<VideoSource> captor = ArgumentCaptor.forClass(VideoSource.class);
        verify(videoSourceService, times(1)).save(captor.capture());
        VideoSource saved = captor.getValue();
        assertEquals(2L, saved.getVideoId());
        assertEquals("mp4", saved.getType());
        assertEquals("only-mp4", saved.getSrc());
    }

    @Test
    void getResourceById_mapsTypesBackToDto() {
        VideoSource mp4 = VideoSource.builder().videoId(1L).type("MP4").src("a").build();
        VideoSource hls = VideoSource.builder().videoId(1L).type("hls").src("b").build();
        VideoSource dash = VideoSource.builder().videoId(1L).type("dash").src("c").build();
        VideoSource unknown = VideoSource.builder().videoId(1L).type("webm").src("z").build();
        when(videoSourceService.list(org.mockito.ArgumentMatchers.<Wrapper<VideoSource>>any()))
                .thenReturn(List.of(mp4, hls, dash, unknown));

        ResourceDto loaded = service.getResourceById(1L);

        assertEquals(1L, loaded.getId());
        assertEquals("a", loaded.getMp4());
        assertEquals("b", loaded.getM3u8());
        assertEquals("c", loaded.getMpd());
        assertNull(loaded.getMd5());
    }

    @Test
    void saveResource_storesPublicPathAndDropsPresignedQuery() {
        when(videoSourceService.exists(any())).thenReturn(false);
        String stable = "http://localhost:9010/transcoded/hls/"
                + "0123456789abcdef0123456789abcdef/0123456789abcdef0123456789abcdef.m3u8";
        String presigned = "  " + stable + "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Signature="
                + "a".repeat(2500) + "#frag";
        assertTrue(presigned.length() > 2048);
        assertTrue(stable.length() <= 2048);

        service.saveResource(ResourceDto.builder().id(9L).mp4(presigned).m3u8(stable).build());

        ArgumentCaptor<VideoSource> captor = ArgumentCaptor.forClass(VideoSource.class);
        verify(videoSourceService, times(2)).save(captor.capture());
        VideoSource mp4 = captor.getAllValues().stream()
                .filter(s -> "mp4".equals(s.getType())).findFirst().orElseThrow();
        VideoSource hls = captor.getAllValues().stream()
                .filter(s -> "hls".equals(s.getType())).findFirst().orElseThrow();
        assertEquals(stable, mp4.getSrc());
        assertEquals(stable, hls.getSrc());
        assertFalse(mp4.getSrc().contains("?"));
        assertFalse(mp4.getSrc().contains("X-Amz-"));
        assertTrue(mp4.getSrc().startsWith("http://localhost:9010/transcoded/"));
    }

    @Test
    void saveResource_rejectsPlaybackUrlLongerThanColumn() {
        String tooLong = "http://localhost:9010/raw/" + "k".repeat(2048);
        assertTrue(tooLong.length() > 2048);
        assertThrows(IllegalArgumentException.class,
                () -> service.saveResource(ResourceDto.builder().id(3L).mp4(tooLong).build()));
        verify(videoSourceService, never()).save(any());
    }

    @Test
    void saveResource_rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.saveResource(null));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveResource(ResourceDto.builder().mp4("a").build()));
        assertThrows(IllegalArgumentException.class, () -> service.getResourceById(null));
    }
}
