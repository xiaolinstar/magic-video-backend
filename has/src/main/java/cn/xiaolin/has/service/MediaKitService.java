package cn.xiaolin.has.service;

import java.io.File;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频转hls、dash
 * @create 2024/11/11
 */
public interface MediaKitService {
    /**
     * 视频（默认：1080p mp4格式）转hls
     * @param videoUrl 视频源url
     */
    void media2Hls(String videoUrl);

    /**
     * 视频（默认：1080p mp4格式）转dash
     * @param videoUrl 视频源dash
     */
    void media2Dash(String videoUrl);
}
