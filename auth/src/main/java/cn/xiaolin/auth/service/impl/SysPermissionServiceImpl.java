package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysPermReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.domain.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
* @author xlxing
* @description 针对表【sys_permission(系统权限)】的数据库操作Service实现
* @createDate 2023-08-10 22:24:12
*/
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
    implements SysPermissionService{

    private final TransactionTemplate transactionTemplate;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public Optional<SysPermission> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysPermission> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<SysPermission> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<SysPermission> updateAndReturn(SysPermReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        LambdaUpdateWrapper<SysPermission> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .set(Objects.nonNull(dto.getName()), SysPermission::getName, dto.getName())
                .set(Objects.nonNull(dto.getDescription()), SysPermission::getDescription, dto.getDescription())
                .set(Objects.nonNull(dto.getView()), SysPermission::getView, dto.getView())
                .set(Objects.nonNull(dto.getModify()), SysPermission::getModify, dto.getModify())
                .eq(SysPermission::getId, dto.getId());

        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<SysPermission> saveAndReturn(SysPermReqDto dto) {
        SysPermission sysPermission = SysPermission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .view(dto.getView())
                .modify(dto.getModify())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysPermission);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysPermission) : Optional.empty();
    }

    /**
     * 查询角色具有的所有权限
     *
     * @param roleId 角色id
     * @return 系统权限
     */
    @Override
    public List<SysPermission> listPermByRoleId(Long roleId) {
        return sysPermissionMapper.listPermByRoleId(roleId);
    }

    /**
     * 查询用户userId的权限
     *
     * @param userId 用户id
     * @return 系统权限
     */
    @Override
    public List<SysPermission> listPermsByUserId(Long userId) {
        return sysPermissionMapper.listPermsByUserId(userId);
    }

    /**
     * 查询用户userId的所有权限，包含角色拥有的权限
     *
     * @param userId 用户id
     * @return 全部权限
     */
    @Override
    public Set<SysPermission> listPermsWithRoleByUserId(Long userId) {
        return sysPermissionMapper.listPermsWithRoleByUserId(userId);
    }
}




