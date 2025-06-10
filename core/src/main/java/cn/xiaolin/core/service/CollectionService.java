package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.Collection;
import cn.xiaolin.core.domain.vo.CollectionVO;
import cn.xiaolin.core.domain.vo.SlideVO;
import cn.xiaolin.core.domain.vo.VideoSetVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xingxiaolin
*/
public interface CollectionService extends IService<Collection> {

    List<CollectionVO> getCollectionList();

    VideoSetVO getVideoSet();

    List<SlideVO> getSlideList();

}
