package cn.xiaolin.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.xiaolin.auth.domain.dto.SysUserReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.service.SysUserService;
import cn.xiaolin.auth.domain.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-08-10 22:24:12
*/
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    private final SysUserMapper sysUserMapper;

    private final TransactionTemplate transactionTemplate;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Optional<SysUser> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<SysUser> deleteAndReturnById(Long id) {
        return transactionTemplate.execute(status -> {
            Optional<SysUser> result = findItemById(id);
            removeById(id);
            return result;
        });
    }

    @Override
    public Optional<SysUser> updateAndReturn(SysUserReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getAdmission()), SysUser::getAdmission, dto.getAdmission())
                .set(Objects.nonNull(dto.getEmail()), SysUser::getEmail, dto.getEmail())
                .set(Objects.nonNull(dto.getPhone()), SysUser::getPhone, dto.getPhone())
                .set(Objects.nonNull(dto.getPassword()), SysUser::getPassword, passwordEncoder.encode(dto.getPassword()))
                .set(Objects.nonNull(dto.getUsername()), SysUser::getUsername, dto.getUsername())
                .eq(SysUser::getId, dto.getId());
        return transactionTemplate.execute(status -> {
            boolean updated = update(updateWrapper);
            return updated ? findItemById(dto.getId()) : Optional.empty();
        });
    }

    @Override
    public Optional<SysUser> saveAndReturn(SysUserReqDto dto) {
        SysUser sysUser = SysUser.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .admission(dto.getAdmission())
                .password(dto.getPassword())
                .build();
        boolean saved = false;
        try {
            saved = this.save(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(sysUser) : Optional.empty();
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户注册信息
     */
    @Override
    public Optional<SysUser> register(String username, String password) {
        SysUserReqDto dto = new SysUserReqDto();
        dto.setUsername(username);
        String encodedPassword = passwordEncoder.encode(password);
        dto.setPassword(encodedPassword);
        return saveAndReturn(dto);
    }


    /**
     * 用户 登陆
     *
     * @param username 用户名
     * @param password 密码
     * @return 登陆消息
     */
    @Override
    public boolean login(String username, String password) {
        Optional<SysUser> user = sysUserMapper.getUserByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            StpUtil.login(user.get().getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }


    /**
     * 查询具有角色roleId的所有用户
     *
     * @param roleId 角色id
     * @return 系统用户列表
     */
    @Override
    public List<SysUser> listUsersByRoleId(Long roleId) {
        return sysUserMapper.listUsersByRoleId(roleId);
    }

    /**
     * 查询具有权限permId的所有用户
     *
     * @param permId 权限id
     * @return 系统用户列表
     */
    @Override
    public List<SysUser> listUsersByPermId(Long permId) {
        return sysUserMapper.listUsersByPermId(permId);
    }

    /**
     * 查询具有权限permId的所有用户
     *
     * @param permId 权限id
     * @return 系统用户列表
     */
    @Override
    public List<SysUser> listUsersWithRoleByPermId(Long permId) {
        return sysUserMapper.listUsersWithRoleByPermId(permId);
    }
}




