package cn.xiaolin.has.service;

import java.nio.file.Path;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频转hls、dash
 * @create 2024/11/11
 */
public interface MediaKitService {

    /**
     * 视频（默认：1080p mp4格式）转 HLS。
     *
     * @param videoUrl    视频源 url
     * @param resourceKey 资源标识，同时作为幂等键：产物已存在时跳过转码直接复用
     * @return 生成的 m3u8 播放地址（公开对象 URL，不含预签名查询串）
     */
    String media2Hls(String videoUrl, String resourceKey);

    /**
     * 视频（默认：1080p mp4格式）转 DASH。
     *
     * @param videoUrl    视频源 url
     * @param resourceKey 资源标识，同时作为幂等键：产物已存在时跳过转码直接复用
     * @return 生成的 mpd 播放地址（公开对象 URL，不含预签名查询串）
     */
    String media2Dash(String videoUrl, String resourceKey);

    /**
     * 上传文件夹下的内容到 minio
     *
     * @param dirPath    文件夹路径
     * @param bucketName 目标 bucket
     */
    void uploadFolder2Minio(Path dirPath, String bucketName);
}
