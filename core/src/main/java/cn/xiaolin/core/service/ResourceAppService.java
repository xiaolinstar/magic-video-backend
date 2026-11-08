package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.ResourceDto;

/**
 * 播放源写入与读回。{@code id} 对应 {@code video_source.video_id}。
 */
public interface ResourceAppService {

    ResourceDto getResourceById(Long id);

    void saveResource(ResourceDto resourceDto);
}
