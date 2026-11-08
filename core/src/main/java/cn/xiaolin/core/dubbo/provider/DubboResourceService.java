package cn.xiaolin.core.dubbo.provider;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.api.dubbo.service.ResourceService;
import cn.xiaolin.core.service.ResourceAppService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Dubbo 入口暂时委托给 {@link ResourceAppService}，Task 7 删除本类。
 */
@DubboService
@RequiredArgsConstructor
public class DubboResourceService implements ResourceService {

    private final ResourceAppService resourceAppService;

    @Override
    public ResourceDto getResourceById(Long id) {
        return toApi(resourceAppService.getResourceById(id));
    }

    @Override
    public void saveResource(ResourceDto resourceDto) {
        resourceAppService.saveResource(toCore(resourceDto));
    }

    private static cn.xiaolin.core.domain.dto.ResourceDto toCore(ResourceDto source) {
        if (source == null) {
            return null;
        }
        return cn.xiaolin.core.domain.dto.ResourceDto.builder()
                .id(source.getId())
                .md5(source.getMd5())
                .mp4(source.getMp4())
                .m3u8(source.getM3u8())
                .mpd(source.getMpd())
                .build();
    }

    private static ResourceDto toApi(cn.xiaolin.core.domain.dto.ResourceDto source) {
        if (source == null) {
            return null;
        }
        return ResourceDto.builder()
                .id(source.getId())
                .md5(source.getMd5())
                .mp4(source.getMp4())
                .m3u8(source.getM3u8())
                .mpd(source.getMpd())
                .build();
    }
}
