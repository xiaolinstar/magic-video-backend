package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoMakerReqDto;
import cn.xiaolin.core.service.VideoMakerService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.VideoMaker;
import cn.xiaolin.core.domain.mapper.VideoMakerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video_maker(视频制作人)】的数据库操作Service实现
* @createDate 2023-07-30 17:10:49
*/
@Service
@RequiredArgsConstructor
public class VideoMakerServiceImpl extends ServiceImpl<VideoMakerMapper, VideoMaker>
    implements VideoMakerService {

    @Override
    public Optional<VideoMaker> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<VideoMaker> deleteAndReturnById(Long id) {
        Optional<VideoMaker> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<VideoMaker> updateAndReturn(VideoMakerReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<VideoMaker> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), VideoMaker::getName, dto.getName())
                .set(Objects.nonNull(dto.getIcon()), VideoMaker::getIcon, dto.getIcon())
                .set(Objects.nonNull(dto.getIntroduction()), VideoMaker::getIntroduction, dto.getIntroduction())
                .set(Objects.nonNull(dto.getGender()), VideoMaker::getGender, dto.getGender())
                .set(Objects.nonNull(dto.getConstellation()), VideoMaker::getConstellation, dto.getConstellation())
                .set(Objects.nonNull(dto.getBirthday()), VideoMaker::getBirthday, dto.getBirthday())
                .set(Objects.nonNull(dto.getIsActor()), VideoMaker::getIsActor, dto.getIsActor())
                .set(Objects.nonNull(dto.getIsDirector()), VideoMaker::getIsDirector, dto.getIsDirector())
                .set(Objects.nonNull(dto.getIsScripter()), VideoMaker::getIsScripter, dto.getIsScripter())
                .set(Objects.nonNull(dto.getPhotoPath()), VideoMaker::getPhotoPath, dto.getPhotoPath())
                .eq(VideoMaker::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<VideoMaker> saveAndReturn(VideoMakerReqDto dto) {
        VideoMaker videoMaker = VideoMaker.builder()
                .id(dto.getId())
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .icon(dto.getIcon())
                .constellation(dto.getConstellation())
                .gender(dto.getGender())
                .isActor(dto.getIsActor())
                .isScripter(dto.getIsScripter())
                .isDirector(dto.getIsDirector())
                .photoPath(dto.getPhotoPath())
                .introduction(dto.getIntroduction())
                .build();
        boolean saved = false;
        try {
            saved = this.save(videoMaker);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.ofNullable(videoMaker) : Optional.empty();
    }
}




