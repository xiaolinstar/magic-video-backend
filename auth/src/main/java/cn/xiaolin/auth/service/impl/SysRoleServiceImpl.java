package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysRoleReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysRoleService;
import cn.xiaolin.auth.domain.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 该类实现了SysRoleService接口，提供了根据id查询角色、删除角色、更新角色、保存角色、查询角色列表的功能。
 * @create 2023/08/10
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    private final SysRoleMapper sysRoleMapper;


    @Override
    public Optional<SysRole> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysRole> deleteAndReturnById(Long id) {
        Optional<SysRole> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<SysRole> updateAndReturn(SysRoleReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), SysRole::getName, dto.getName())
                .eq(SysRole::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<SysRole> saveAndReturn(SysRoleReqDto dto) {
        SysRole sysRole = SysRole.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysRole);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysRole) : Optional.empty();
    }

    /**
     * 查询具有权限id的所有角色
     *
     * @param permId 权限id
     * @return 系统角色列表
     */
    @Override
    public List<SysRole> listRolesByPermId(Long permId) {
        return sysRoleMapper.listRolesByPermId(permId);
    }

    /**
     * 查询用户id的所有角色
     *
     * @param userId 用户id
     * @return 系统角色列表
     */
    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        return sysRoleMapper.listRolesByUserId(userId);
    }
}




