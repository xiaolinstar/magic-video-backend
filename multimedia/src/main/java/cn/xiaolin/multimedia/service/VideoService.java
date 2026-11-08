package cn.xiaolin.multimedia.service;

import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频上传到服务器，并保存一些信息
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频上传服务
 * @create 2023/7/23
 */
public interface VideoService {

    /**
     * 上传整文件视频到对象存储，并投递转码任务。
     *
     * @param video     视频文件
     * @param videoType 视频类型
     * @param videoId   关联的影视 id（对应 {@code video_source.video_id}），不可为空
     * @return 稳定播放地址（公开对象 URL，不含预签名查询串）
     */
    String uploadVideoMinio(MultipartFile video, VideoTypeEnum videoType, Long videoId);

    /**
     * 视频分片上传
     *
     * @param chunkVideo 分片文件
     * @param md5        视频标识
     * @param chunkId    分片序号
     * @param chunkMd5   分片摘要值
     * @return 下一个分片序号
     */
    Long uploadVideoChunk(MultipartFile chunkVideo, String md5, long chunkId, String chunkMd5);

    /**
     * 取消分片上传，清理已上传的分片。
     */
    Boolean abortUpload(String md5);

    /**
     * 合并分片、上传到对象存储并投递转码任务。
     *
     * @param md5       视频标识（分片目录名）
     * @param videoType 视频类型
     * @param fileName  合并后的文件名（不含后缀）
     * @param videoId   关联的影视 id，不可为空
     */
    void videoChunksMerge(String md5, VideoTypeEnum videoType, String fileName, Long videoId);

    /**
     * 合并分片、上传到对象存储并投递转码任务（文件名取 md5）。
     */
    void videoChunksMerge(String md5, VideoTypeEnum videoType, Long videoId);
}
