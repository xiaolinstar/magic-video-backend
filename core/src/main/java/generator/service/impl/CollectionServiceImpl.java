package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Collection;
import generator.service.CollectionService;
import generator.mapper.CollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author xlxing
* @description 针对表【collection(视频合集)】的数据库操作Service实现
* @createDate 2025-06-08 17:46:29
*/
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
    implements CollectionService{

}




