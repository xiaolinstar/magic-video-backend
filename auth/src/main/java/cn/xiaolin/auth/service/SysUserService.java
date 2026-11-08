package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysUserReqDto;
import cn.xiaolin.auth.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统用户服务接口
 * @create 2023/08/10
 */
public interface SysUserService extends IService<SysUser> {

    Optional<SysUser> findItemById(Long id);

    Optional<SysUser> deleteAndReturnById(Long id);

    Optional<SysUser> updateAndReturn(SysUserReqDto dto);

    Optional<SysUser> saveAndReturn(SysUserReqDto dto);

    /**
     * 直接重设密码，**不做强度校验**。
     *
     * <p>仅供「历史明文密码迁移」等内部场景使用：数据库中已存在的历史密码
     * （如 {@code 123456xxl}）可能不满足当前的密码强度规则，而强度规则是为了
     * 约束用户的新输入。若迁移路径复用 {@link #updateAndReturn(SysUserReqDto)}，
     * 弱口令历史数据会让 auth 服务在启动时直接失败。
     *
     * @param id         用户 id
     * @param rawPassword 原始（未加密）密码
     * @return 更新后的用户
     */
    Optional<SysUser> resetPasswordWithoutStrengthCheck(Long id, String rawPassword);

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @return 用户注册信息
     */
    Optional<SysUser> register(String username, String password);

    /**
     * 用户 登陆
     * @param username 用户名
     * @param password 密码
     * @return 登陆消息
     */
    boolean login(String username, String password);

    void logout();

    void kickOut(Long userId);

    /**
     * 查询具有角色roleId的所有用户
     * @param roleId 角色id
     * @return 系统用户列表
     */
    List<SysUser> listUsersByRoleId(Long roleId);

    /**
     * 查询具有权限permId的所有用户
     * @param permId 权限id
     * @return 系统用户列表
     */
    List<SysUser> listUsersByPermId(Long permId);

    /**
     * 查询具有权限permId的所有用户
     * @param permId 权限id
     * @return 系统用户列表
     */
    List<SysUser> listUsersWithRoleByPermId(Long permId);
}
