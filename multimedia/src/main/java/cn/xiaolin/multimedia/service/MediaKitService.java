package cn.xiaolin.multimedia.service;

import java.io.File;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 媒体工具 服务类
 * @create 2023/7/23
 */
public interface MediaKitService {

    /**
     *
     * @param file 视频文件
     */
    void media2Hls(File file);

    void media2Dash(File file);

}
