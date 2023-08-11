package cn.xiaolin.multimedia.service;

import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 视频上传到服务器，并保存一些信息
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 视频上传服务
 * @create 2023/7/23
 */
public interface VideoService {

    /**
     * 文件上传
     * @param file MultipartFile格式文件
     * @return 文件id
     */
    Long videoUpload(MultipartFile file);

    /**
     * 视频分片上传
     * @param requestDTO 上传请求
     * @return 上传消息
     */
    FileSliceUploadVo sliceVideoUpload(SliceFileUploadRequestDto requestDTO);

    /**
     * 视频合并
     * @param chunks 视频分片数
     * @param md5 视频摘要md5
     * @return 视频资源ID
     */
    Long sliceVideoMerge(Integer chunks, String md5, String suffix);

    /**
     * 视频上传校验
     * @param md5 视频摘要
     * @return 视频资源ID
     */
    Long checkVideo(String md5);

    /**
     * 暂停后继续上传，返回已经上传的切片序号
     * @param md5 视频摘要md5
     * @return 已经上传的分片序号
     */
    Set<Integer> continueUpload(String md5);
}
