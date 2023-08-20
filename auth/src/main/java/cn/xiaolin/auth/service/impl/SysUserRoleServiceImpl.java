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
* @author xlxing
* @description 针对表【sys_user_role(系统用户角色关联表)】的数据库操作Service实现
* @createDate 2023-08-10 22:24:12
*/
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
    implements SysUserRoleService{
    private final TransactionTemplate transactionTemplate;

    @Override
    public Optional<SysUserRole> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysUserRole> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<SysUserRole> result = findItemById(id);
            removeById(id);
            return result;
        });
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
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
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




