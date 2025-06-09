package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.domain.vo.VideoVO;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频详情控制器
 * @create 2023/7/30
 */
@Tag(name = "视频详情", description = "视频详情增删改查基本操作")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "查询视频资源")
    @GetMapping("/video/list")
    public Result<List<VideoVO>> all() {
        List<VideoVO> videoList = videoService.getVideoList();
        return Result.ok(videoList);
    }
}
