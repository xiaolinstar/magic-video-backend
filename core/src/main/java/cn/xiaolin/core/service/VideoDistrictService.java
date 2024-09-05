package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoDirectorReqDto;
import cn.xiaolin.core.domain.dto.VideoDistrictReqDto;
import cn.xiaolin.core.domain.entity.VideoDirector;
import cn.xiaolin.core.domain.entity.VideoDistrict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_district(视频地区关联关系表)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoDistrictService extends IService<VideoDistrict> {
    Optional<VideoDistrict> findItemById(Long id);

    Optional<VideoDistrict> deleteAndReturnById(Long id);

    Optional<VideoDistrict> updateAndReturn(VideoDistrictReqDto dto);

    Optional<VideoDistrict> saveAndReturn(VideoDistrictReqDto dto);
}
