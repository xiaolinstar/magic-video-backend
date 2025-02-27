package cn.xiaolin.multimedia.service;

import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频上传到服务器，并保存一些信息
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频上传服务
 * @create 2023/7/23
 */
public interface VideoService {


    String uploadVideoMinio(MultipartFile video);

    String uploadVideoMinio(String videoName);

    Long uploadVideoChunk(MultipartFile chunkVideo, String md5, long chunkId, String chunkMd5);

    Boolean abortUpload(String md5);

    void videoChunksMerge(String md5, VideoTypeEnum videoType);

}
