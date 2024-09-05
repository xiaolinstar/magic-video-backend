package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.dto.SysUserRoleReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysUserRole;
import cn.xiaolin.auth.service.SysUserRoleService;
import cn.xiaolin.auth.domain.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 该类实现了SysUserRoleService接口，提供了根据id查询用户角色、删除用户角色、更新用户角色、保存用户角色的功能。
 * @create 2023/08/10
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
    implements SysUserRoleService{
    @Override
    public Optional<SysUserRole> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysUserRole> deleteAndReturnById(Long id) {
        Optional<SysUserRole> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<SysUserRole> updateAndReturn(SysUserRoleReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<SysUserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getSysRoleId()), SysUserRole::getSysRoleId, dto.getSysUserId())
                .set(Objects.nonNull(dto.getSysUserId()), SysUserRole::getSysUserId, dto.getSysUserId())
                .eq(SysUserRole::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<SysUserRole> saveAndReturn(SysUserRoleReqDto dto) {
        SysUserRole sysUserRole = SysUserRole.builder()
                .id(dto.getId())
                .sysRoleId(dto.getSysRoleId())
                .sysUserId(dto.getSysUserId())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysUserRole);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysUserRole) : Optional.empty();
    }
}




