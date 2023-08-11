package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoDistrictReqDto;
import cn.xiaolin.core.service.VideoDistrictService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoDistrict;
import cn.xiaolin.core.domain.mapper.VideoDistrictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_district(视频地区关联关系表)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoDistrictServiceImpl extends ServiceImpl<VideoDistrictMapper, VideoDistrict>
    implements VideoDistrictService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<VideoDistrict> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoDistrict> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<VideoDistrict> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<VideoDistrict> updateAndReturn(VideoDistrictReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoDistrict> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getDistrictId()), VideoDistrict::getDistrictId, dto.getDistrictId())
                .set(Objects.nonNull(dto.getVideoId()), VideoDistrict::getVideoId, dto.getVideoId())
                .eq(VideoDistrict::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<VideoDistrict> saveAndReturn(VideoDistrictReqDto dto) {
        VideoDistrict videoDistrict = VideoDistrict.builder()
                .id(dto.getId())
                .videoId(dto.getVideoId())
                .districtId(dto.getDistrictId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoDistrict);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(videoDistrict) : Optional.empty();
    }
}




