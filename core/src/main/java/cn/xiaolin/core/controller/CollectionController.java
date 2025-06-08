package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.vo.CollectionVO;
import cn.xiaolin.core.service.CollectionService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频集合
 * @create 2025/6/8
 */
@Tag(name = "视频集合")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class CollectionController {
    private final CollectionService collectionService;

    @Operation(summary = "查询视频集合")
    @GetMapping("/collection/list")
    public Result<List<CollectionVO>> all() {
        return Result.ok(collectionService.getCollectionList());
    }
}
