package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.service.ResourceService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Resource;
import cn.xiaolin.core.domain.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【resource(视频资源)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements ResourceService {

    @Override
    public Optional<Resource> findItemById(Long id) {
        Resource resource = this.getById(id);
        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(resource);
    }

    @Override
    public Optional<Resource> deleteAndReturnById(Long id) {
        Optional<Resource> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<Resource> updateAndReturn(ResourceReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("id 不允许为 null");
        }
        LambdaUpdateWrapper<Resource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), Resource::getName, dto.getName())
                .set(Objects.nonNull(dto.getMpd()), Resource::getMpd, dto.getMpd())
                .set(Objects.nonNull(dto.getMp4()), Resource::getMp4, dto.getMp4())
                .set(Objects.nonNull(dto.getM3u8()), Resource::getM3u8, dto.getM3u8())
                .set(Objects.nonNull(dto.getMd5()), Resource::getMd5, dto.getMd5())
                .set(Objects.nonNull(dto.getAvatar()), Resource::getAvatar, dto.getAvatar())
                .set(Objects.nonNull(dto.getTitle()), Resource::getTitle, dto.getTitle())
                .set(Objects.nonNull(dto.getDescription()), Resource::getDescription, dto.getDescription())
                .eq(Resource::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<Resource> saveAndReturn(ResourceReqDto dto) {
        Resource resource = Resource.builder()
                .id(dto.getId())
                .name(dto.getName())
                .mp4(dto.getMp4())
                .m3u8(dto.getM3u8())
                .mpd(dto.getMpd())
                .md5(dto.getMd5())
                .avatar(dto.getAvatar())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
        boolean saved = false;
        try {
            saved = this.save(resource);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.ofNullable(resource) : Optional.empty();
    }
}





