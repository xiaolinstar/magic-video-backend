package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.Season;
import cn.xiaolin.core.domain.vo.SeasonVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;

/**
* @author xingxiaolin
*/
public interface SeasonService extends IService<Season> {
    List<SeasonVO> getSeasonList();

    Optional<SeasonVO> getSeasonById(Long id);
}
