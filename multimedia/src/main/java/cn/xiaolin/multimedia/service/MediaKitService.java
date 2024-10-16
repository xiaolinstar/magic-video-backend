package cn.xiaolin.multimedia.service;

import java.io.File;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 媒体工具 服务类
 * @create 2023/7/23
 */
public interface MediaKitService {

    void media2Hls(File video);

    void media2Dash(File video);

}
