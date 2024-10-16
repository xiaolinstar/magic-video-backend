package cn.xiaolin.multimedia.service;

import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 视频上传到服务器，并保存一些信息
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频上传服务
 * @create 2023/7/23
 */
public interface VideoService {


    String videoUpload(MultipartFile video);

    Boolean initSliceUpload(String md5, long chunkNum);

    long sliceVideoUpload(SliceFileUploadRequestDto requestDTO);

    Boolean abortUpload(String md5);

    String sliceVideoMerge(String md5);
}
