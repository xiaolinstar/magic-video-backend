package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.AwardReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Award;
import cn.xiaolin.core.service.AwardService;
import cn.xiaolin.core.domain.mapper.AwardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【award(奖项)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award>
    implements AwardService {

    private final TransactionTemplate transactionTemplate;

    /**
     * 根据id查询奖项信息
     *
     * @param id 奖项id
     * @return 查询到的奖项
     */
    @Override
    public Optional<Award> findItemById(Long id) {
        return Optional.ofNullable(this.getById(id));
    }

    /**
     * 根据id删除奖项信息
     *
     * @param id 奖项id
     * @return 删除的奖项
     */
    @Override
    public Optional<Award> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<Award> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    /**
     * TODO 考虑外键的依赖关系
     * TODO 直接插入，失败异常处理
     * 更新奖项信息
     *
     * @param dto 更新信息
     * @return 更新后的奖项信息
     */
    @Override
    public Optional<Award> updateAndReturn(AwardReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<Award> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), Award::getName, dto.getName())
                .set(Objects.nonNull(dto.getIcon()), Award::getIcon, dto.getIcon())
                .set(Objects.nonNull(dto.getYear()), Award::getYear, dto.getYear())
                .set(Objects.nonNull(dto.getCountry()), Award::getCountry, dto.getCountry())
                .set(Objects.nonNull(dto.getBestDirectorId()), Award::getBestDirectorId, dto.getBestDirectorId())
                .set(Objects.nonNull(dto.getBestDirectorNominationId()), Award::getBestDirectorNominationId, dto.getBestDirectorNominationId())
                .set(Objects.nonNull(dto.getBestMaleActorId()), Award::getBestMaleActorId, dto.getBestMaleActorId())
                .set(Objects.nonNull(dto.getBestMaleActorNominationId()), Award::getBestMaleActorNominationId, dto.getBestMaleActorNominationId())
                .set(Objects.nonNull(dto.getBestFemaleActorId()), Award::getBestFemaleActorId, dto.getBestFemaleActorId())
                .set(Objects.nonNull(dto.getBestFemaleActorNominationId()), Award::getBestFemaleActorNominationId, dto.getBestFemaleActorNominationId())
                .set(Objects.nonNull(dto.getBestScripterId()), Award::getBestScripterId, dto.getBestScripterId())
                .set(Objects.nonNull(dto.getBestScripterNominationId()), Award::getBestScripterNominationId, dto.getBestScripterNominationId())
                .set(Objects.nonNull(dto.getBestNewActorId()), Award::getBestNewActorId, dto.getBestNewActorId())
                .eq(Award::getId, dto.getId());

        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    /**
     * 新增奖项信息
     *
     * @param dto 奖项信息
     * @return 新增的奖项信息
     */
    @Override
    public Optional<Award> saveAndReturn(AwardReqDto dto) {
        Award award = Award.builder()
                .id(dto.getId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .year(dto.getYear())
                .country(dto.getCountry())
                .bestDirectorId(dto.getBestDirectorId())
                .bestDirectorNominationId(dto.getBestDirectorNominationId())
                .bestMaleActorId(dto.getBestMaleActorId())
                .bestFemaleActorId(dto.getBestFemaleActorId())
                .bestMaleActorNominationId(dto.getBestMaleActorNominationId())
                .bestFemaleActorNominationId(dto.getBestFemaleActorNominationId())
                .bestScripterId(dto.getBestScripterId())
                .bestScripterNominationId(dto.getBestScripterNominationId())
                .bestNewActorId(dto.getBestNewActorId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(award);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(award) : Optional.empty();
    }

}


