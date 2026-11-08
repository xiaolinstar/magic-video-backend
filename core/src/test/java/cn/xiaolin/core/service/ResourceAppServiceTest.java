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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    void saveResource_rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.saveResource(null));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveResource(ResourceDto.builder().mp4("a").build()));
        assertThrows(IllegalArgumentException.class, () -> service.getResourceById(null));
    }
}
