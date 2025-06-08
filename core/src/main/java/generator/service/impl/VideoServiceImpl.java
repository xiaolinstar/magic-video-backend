package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Video;
import generator.service.VideoService;
import generator.mapper.VideoMapper;
import org.springframework.stereotype.Service;

/**
* @author xlxing
* @description 针对表【video(视频)】的数据库操作Service实现
* @createDate 2025-06-08 17:37:06
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService{

}




