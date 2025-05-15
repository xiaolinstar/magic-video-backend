package cn.xiaolin.multimedia.controller;

import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.multimedia.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 上传视频控制器
 * @create 2023/7/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_MULTIMEDIA_PREFIX)
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/video/chunk")
    public Result<Map<String, Long>> one(@RequestParam MultipartFile chunkVideo,
                                         @RequestParam String md5,
                                         @RequestParam long chunkId,
                                         @RequestParam(required = false) String chunkMd5) {
        long nextChunkId = videoService.uploadVideoChunk(chunkVideo, md5, chunkId, chunkMd5);
        return Result.ok(Map.of("nextChunkId", nextChunkId));
    }

    @PostMapping("/video/merge")
    public Result<Map<String, Long>> sliceMerge(@RequestParam String md5,
                                                @RequestParam VideoTypeEnum videoType,
                                                @RequestParam(required = false) String filename) {
        videoService.videoChunksMerge(md5, videoType, filename);
        return Result.ok();
    }

    @GetMapping("/video/check")
    public Result<Long> checkOne(@RequestParam String md5) {
        throw new NotImplementedException();
    }


    @PostMapping("/video")
    public Result<Map<String, String>> one(MultipartFile video, VideoTypeEnum videoType) {
        String videoUrl = videoService.uploadVideoMinio(video, videoType);
        return Result.ok(Map.of("url", videoUrl));
    }
}
