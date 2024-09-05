package cn.xiaolin.multimedia.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import cn.xiaolin.multimedia.service.MediaKitService;
import cn.xiaolin.multimedia.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public Result<FileSliceUploadVo> one(@RequestParam MultipartFile file, @RequestParam Integer chunk,
                                         @RequestParam Integer chunks, @RequestParam String md5) {
        SliceFileUploadRequestDto requestDTO = SliceFileUploadRequestDto.builder()
                .file(file)
                .chunk(chunk)
                .chunks(chunks)
                .md5(md5)
                .build();
        FileSliceUploadVo result = videoService.sliceVideoUpload(requestDTO);
        return Result.ok(result);
    }

    @PostMapping("/video/merge")
    public Result<Map<String, Long>> sliceMerge(@RequestParam Integer chunks, @RequestParam String md5, String suffix) {
        Long resourceId = videoService.sliceVideoMerge(chunks, md5, suffix);
        Map<String, Long> map = new HashMap<>();
        map.put("resourceId", resourceId);
        return Result.ok(map);
    }

    @GetMapping("/video/check")
    public Result<Long> checkOne(@RequestParam String md5) {
        throw new NotImplementedException();
    }

    @GetMapping("/video/resume")
    public Result<Set<Integer>> resume(@RequestParam String md5) {
        Set<Integer> received = videoService.continueUpload(md5);
        return Result.ok(received);
    }

    @PostMapping("/video")
    public Result<Map<String, Long>> one(MultipartFile file) {
        // 上传文件
        Long resourceId = videoService.videoUpload(file);
        Map<String, Long> map = new HashMap<>();
        map.put("id", resourceId);
        return Result.ok(map);
    }
}
