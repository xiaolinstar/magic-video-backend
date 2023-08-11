package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.AwardReqDto;
import cn.xiaolin.core.domain.entity.Award;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * 表的增删改查，如果执行成功，返回数据消息
* @author xlxing
* @description 针对表【award(奖项)】的数据库操作Service
* @createDate 2023-07-30 17:10:49
*/
public interface AwardService extends IService<Award> {

    /**
     * 根据id查询奖项信息
     * @param id 奖项id
     * @return 查询到的奖项
     */
    Optional<Award> findItemById(Long id);

    /**
     * 根据id删除奖项信息
     * @param id 奖项id
     * @return 删除的奖项
     */
    Optional<Award> deleteAndReturnById(Long id);

    /**
     * 更新奖项信息
     * @param dto 更新信息
     * @return 更新后的奖项信息
     */
    Optional<Award> updateAndReturn(AwardReqDto dto);

    /**
     * 新增奖项信息
     * @param dto 奖项信息
     * @return 新增的奖项信息
     */
    Optional<Award> saveAndReturn(AwardReqDto dto);



}
