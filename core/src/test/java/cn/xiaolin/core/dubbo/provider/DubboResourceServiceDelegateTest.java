package cn.xiaolin.core.dubbo.provider;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.core.service.ResourceAppService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DubboResourceServiceDelegateTest {

    @Mock
    private ResourceAppService resourceAppService;

    @InjectMocks
    private DubboResourceService dubboResourceService;

    @Test
    void saveAndGetForwardFields() {
        ResourceDto apiDto = ResourceDto.builder()
                .id(1L)
                .md5("md5")
                .mp4("a")
                .m3u8("b")
                .mpd("c")
                .build();

        dubboResourceService.saveResource(apiDto);

        ArgumentCaptor<cn.xiaolin.core.domain.dto.ResourceDto> captor =
                ArgumentCaptor.forClass(cn.xiaolin.core.domain.dto.ResourceDto.class);
        verify(resourceAppService).saveResource(captor.capture());
        cn.xiaolin.core.domain.dto.ResourceDto forwarded = captor.getValue();
        assertEquals(1L, forwarded.getId());
        assertEquals("md5", forwarded.getMd5());
        assertEquals("a", forwarded.getMp4());
        assertEquals("b", forwarded.getM3u8());
        assertEquals("c", forwarded.getMpd());

        when(resourceAppService.getResourceById(1L)).thenReturn(forwarded);
        ResourceDto loaded = dubboResourceService.getResourceById(1L);
        assertEquals("a", loaded.getMp4());
        assertEquals("b", loaded.getM3u8());
        assertEquals("c", loaded.getMpd());
        assertEquals("md5", loaded.getMd5());
    }
}
