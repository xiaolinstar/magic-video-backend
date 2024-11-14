package cn.xiaolin.multimedia.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
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

    @PostMapping("/video/slice")
    public Result<FileSliceUploadVo> one(@RequestParam MultipartFile chunkVideo,
                                         @RequestParam String md5,
                                         @RequestParam long chunkId,
                                         @RequestParam String chunkMd5) {
        SliceFileUploadRequestDto requestDTO = SliceFileUploadRequestDto.builder()
                .md5(md5)
                .chunkVideo(chunkVideo)
                .chunkId(chunkId)
                .chunkMd5(chunkMd5)
                .build();
        videoService.sliceVideoUpload(requestDTO);
        return Result.ok();
    }

    @PostMapping("/video/merge")
    public Result<Map<String, Long>> sliceMerge(@RequestParam String md5) {
        throw new NotImplementedException();
    }

    @GetMapping("/video/check")
    public Result<Long> checkOne(@RequestParam String md5) {
        throw new NotImplementedException();
    }


    @PostMapping("/video")
    public Result<Map<String, String>> one(MultipartFile video) {
        String videoUrl = videoService.videoUpload(video);
        return Result.ok(Map.of("url", videoUrl));
    }
}
