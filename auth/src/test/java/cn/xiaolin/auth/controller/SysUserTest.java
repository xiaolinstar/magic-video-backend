package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysUserReqDto;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.domain.mapper.SysUserMapper;
import cn.xiaolin.auth.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 用户测试类
 * @create 2024/10/12
 */
@SpringBootTest
@ActiveProfiles("dev")
class SysUserTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void updateSysUserPassword() {
        List<SysUser> sysUserList = sysUserService.list();

        for (SysUser user : sysUserList) {
            if (user.getPassword().startsWith("$2a$")) {
                System.out.printf("用户名%s的密码已经是加密的了", user.getId());
                continue;
            }
            Optional<SysUser> sysUser = sysUserService.updateAndReturn(
                    SysUserReqDto.builder()
                            .id(user.getId())
                            .password(user.getPassword())
                            .build()
            );
            System.out.println(sysUser.orElseThrow());
        }
    }

    @Test
    public void passwordTest() {
        String username = "xiaolin";
        String password = "123456xxl";

        SysUserReqDto reqDto = SysUserReqDto.builder()
                .username(username)
                .password(password)
                .admission(Boolean.TRUE)
                .build();
        Optional<SysUser> sysUser = sysUserService.saveAndReturn(reqDto);
        System.out.println(sysUser.orElseThrow());

        System.out.println(passwordEncoder.matches(password, sysUser.orElseThrow().getPassword()));
    }
}