package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.DistrictReqDto;
import cn.xiaolin.core.domain.entity.District;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【district(国家和地区)】的数据库操作Service
* @createDate 2023-07-30 17:10:49
*/
public interface DistrictService extends IService<District> {
    Optional<District> findItemById(Long id);
    Optional<District> deleteAndReturnById(Long id);

    Optional<District> updateAndReturn(DistrictReqDto dto);

    Optional<District> saveAndReturn(DistrictReqDto dto);
}
