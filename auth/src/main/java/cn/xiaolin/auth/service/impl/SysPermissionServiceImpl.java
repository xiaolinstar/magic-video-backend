package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysPermReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.domain.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统权限服务实现类
 * @create 2023/08/10
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
    implements SysPermissionService{

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public Optional<SysPermission> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysPermission> deleteAndReturnById(Long id) {
        Optional<SysPermission> result = findItemById(id);
        removeById(id);
        return result;
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
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
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
    public List<SysPermission> listPermsByRoleId(Long roleId) {
        return sysPermissionMapper.listPermsByRoleId(roleId);
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
     * 根据username查询用户权限
     *
     * @param username 用户名
     * @return 权限列表
     */
    @Override
    public List<SysPermission> listPermsByUsername(String username) {
        return sysPermissionMapper.listPermsByUsername(username);
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

    /**
     * 根据username查询用户权限，包括通过角色间接拥有的权限
     *
     * @param username 用户名
     * @return 权限列表
     */
    @Override
    public Set<SysPermission> listPermsWithRoleByUsername(String username) {
        return sysPermissionMapper.listPermsWithRoleByUsername(username);
    }
}




