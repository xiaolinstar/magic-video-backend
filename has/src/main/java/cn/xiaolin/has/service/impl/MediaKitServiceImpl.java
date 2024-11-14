package cn.xiaolin.has.service.impl;

import cn.xiaolin.has.service.MediaKitService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频转hls dash，成功后rcp写数据库表resource
 * @create 2024/11/11
 */
@Service
public class MediaKitServiceImpl implements MediaKitService {
    @Override
    public void media2Hls(File video) {

    }

    @Override
    public void media2Dash(File video) {

    }
}
