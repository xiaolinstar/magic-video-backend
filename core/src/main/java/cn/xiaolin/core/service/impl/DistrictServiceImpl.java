package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.DistrictReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.District;
import cn.xiaolin.core.service.DistrictService;
import cn.xiaolin.core.domain.mapper.DistrictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【district(国家和地区)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District>
    implements DistrictService{

    @Override
    public Optional<District> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<District> deleteAndReturnById(Long id) {
        Optional<District> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<District> updateAndReturn(DistrictReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<District> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), District::getName, dto.getName())
                .set(Objects.nonNull(dto.getCapital()), District::getCapital, dto.getCapital())
                .eq(District::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<District> saveAndReturn(DistrictReqDto dto) {
        District district = District.builder()
                .id(dto.getId())
                .name(dto.getName())
                .capital(dto.getCapital())
                .build();
        boolean saved = false;
        try {
            saved = this.save(district);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(district) : Optional.empty();
    }
}




