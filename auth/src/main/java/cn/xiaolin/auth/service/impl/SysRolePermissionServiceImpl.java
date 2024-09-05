package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysRolePermReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysRolePermission;
import cn.xiaolin.auth.service.SysRolePermissionService;
import cn.xiaolin.auth.domain.mapper.SysRolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统角色权限服务实现类
 * @create 2023/08/10
 */
@Service
@RequiredArgsConstructor
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
    implements SysRolePermissionService{

    @Override
    public Optional<SysRolePermission> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysRolePermission> deleteAndReturnById(Long id) {
        Optional<SysRolePermission> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<SysRolePermission> updateAndReturn(SysRolePermReqDto dto) {
        LambdaUpdateWrapper<SysRolePermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getSysRoleId()), SysRolePermission::getSysRoleId, dto.getSysPermissionId())
                .set(Objects.nonNull(dto.getSysPermissionId()), SysRolePermission::getSysPermissionId, dto.getSysPermissionId())
                .eq(SysRolePermission::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<SysRolePermission> saveAndReturn(SysRolePermReqDto dto) {
        SysRolePermission sysRolePermission = SysRolePermission.builder()
                .id(dto.getId())
                .sysRoleId(dto.getSysRoleId())
                .sysPermissionId(dto.getSysPermissionId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysRolePermission);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysRolePermission) : Optional.empty();
    }
}




