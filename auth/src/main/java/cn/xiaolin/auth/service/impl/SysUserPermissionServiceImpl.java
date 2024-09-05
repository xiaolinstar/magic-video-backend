package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysUserPermReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysUserPermission;
import cn.xiaolin.auth.service.SysUserPermissionService;
import cn.xiaolin.auth.domain.mapper.SysUserPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 该类实现了SysUserPermissionService接口，提供了根据id查询用户权限、删除用户权限、更新用户权限、保存用户权限的功能。
 * @create 2023/08/10
 */
@Service
@RequiredArgsConstructor
public class SysUserPermissionServiceImpl extends ServiceImpl<SysUserPermissionMapper, SysUserPermission>
    implements SysUserPermissionService{

    @Override
    public Optional<SysUserPermission> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysUserPermission> deleteAndReturnById(Long id) {
        Optional<SysUserPermission> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<SysUserPermission> updateAndReturn(SysUserPermReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<SysUserPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getSysPermissionId()), SysUserPermission::getSysPermissionId, dto.getSysPermissionId())
                .set(Objects.nonNull(dto.getSysUserId()), SysUserPermission::getSysUserId, dto.getSysUserId())
                .eq(SysUserPermission::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<SysUserPermission> saveAndReturn(SysUserPermReqDto dto) {
        SysUserPermission sysUserPermission = SysUserPermission.builder()
                .id(dto.getId())
                .sysPermissionId(dto.getSysPermissionId())
                .sysUserId(dto.getSysUserId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysUserPermission);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysUserPermission) : Optional.empty();
    }
}




