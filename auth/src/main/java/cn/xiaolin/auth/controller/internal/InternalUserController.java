package cn.xiaolin.auth.controller.internal;

import cn.xiaolin.auth.service.UserPermQueryService;
import cn.xiaolin.utils.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserPermQueryService userPermQueryService;

    @GetMapping("/{id}/permissions")
    public Result<List<String>> permissions(@PathVariable("id") Long id) {
        return Result.ok(userPermQueryService.listPermissionNames(id));
    }

    @GetMapping("/{id}/roles")
    public Result<List<String>> roles(@PathVariable("id") Long id) {
        return Result.ok(userPermQueryService.listRoleNames(id));
    }
}
