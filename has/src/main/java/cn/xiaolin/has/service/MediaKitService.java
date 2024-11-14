package cn.xiaolin.has.service;

import java.io.File;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频转hls、dash
 * @create 2024/11/11
 */
public interface MediaKitService {
    void media2Hls(File video);

    void media2Dash(File video);
}
