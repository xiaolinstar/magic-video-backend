package cn.xiaolin.core.controller.internal;

import cn.xiaolin.core.domain.dto.ResourceDto;
import cn.xiaolin.core.service.ResourceAppService;
import cn.xiaolin.utils.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/resources")
@RequiredArgsConstructor
public class InternalResourceController {

    private final ResourceAppService resourceAppService;

    @PostMapping
    public Result<Void> save(@RequestBody ResourceDto body) {
        resourceAppService.saveResource(body);
        return Result.ok();
    }

    @GetMapping("/{id}")
    public Result<ResourceDto> get(@PathVariable Long id) {
        return Result.ok(resourceAppService.getResourceById(id));
    }
}
