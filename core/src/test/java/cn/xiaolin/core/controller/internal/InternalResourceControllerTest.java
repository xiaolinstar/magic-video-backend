package cn.xiaolin.core.controller.internal;

import cn.xiaolin.core.domain.dto.ResourceDto;
import cn.xiaolin.core.service.ResourceAppService;
import cn.xiaolin.utils.resp.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternalResourceControllerTest {

    @Test
    void saveAndGetDelegateToAppService() {
        ResourceAppService appService = mock(ResourceAppService.class);
        InternalResourceController controller = new InternalResourceController(appService);
        ResourceDto body = ResourceDto.builder().id(9L).mp4("a").m3u8("b").mpd("c").build();

        Result<Void> saved = controller.save(body);

        assertEquals(200, saved.getCode());
        assertEquals("操作成功", saved.getMsg());
        assertNull(saved.getData());
        verify(appService).saveResource(body);

        when(appService.getResourceById(9L)).thenReturn(body);
        Result<ResourceDto> loaded = controller.get(9L);
        assertEquals(200, loaded.getCode());
        assertEquals("操作成功", loaded.getMsg());
        assertEquals(body, loaded.getData());
    }
}
