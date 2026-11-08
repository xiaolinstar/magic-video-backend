package cn.xiaolin.auth.controller.internal;

import cn.xiaolin.auth.service.UserPermQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = InternalUserController.class)
class InternalUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPermQueryService userPermQueryService;

    @Test
    void permissionsReturnsNames() throws Exception {
        when(userPermQueryService.listPermissionNames(1L))
                .thenReturn(List.of("video:read"));

        mockMvc.perform(get("/api/v1/internal/users/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"))
                .andExpect(jsonPath("$.data[0]").value("video:read"));

        verify(userPermQueryService).listPermissionNames(1L);
    }

    @Test
    void rolesReturnsNames() throws Exception {
        when(userPermQueryService.listRoleNames(1L))
                .thenReturn(List.of("ADMIN"));

        mockMvc.perform(get("/api/v1/internal/users/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"))
                .andExpect(jsonPath("$.data[0]").value("ADMIN"));

        verify(userPermQueryService).listRoleNames(1L);
    }
}
