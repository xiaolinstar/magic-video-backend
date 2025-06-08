package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.vo.CollectionVO;
import cn.xiaolin.core.domain.vo.SeasonVO;
import cn.xiaolin.core.service.SeasonService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/6/8
 */
@Tag(name = "剧集季")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class SeasonController {
    private final SeasonService seasonService;

    @Operation(summary = "查询剧集")
    @GetMapping("/season/list")
    public Result<List<SeasonVO>> all() {
        return Result.ok(seasonService.getSeasonList());
    }
}
