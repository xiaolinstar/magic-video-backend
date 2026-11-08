package cn.xiaolin.auth.mapper;

import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.domain.mapper.SysPermissionMapper;
import cn.xiaolin.auth.domain.mapper.SysRoleMapper;
import cn.xiaolin.auth.domain.mapper.SysUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自定义 SQL 的两个回归防线（P0-2 / P0-3）。
 *
 * <p>这两条曾经「声称已修但代码没动」过 —— 文档写着 ✅，mapper 里却还写死库名、
 * 也一条 {@code deleted = 0} 都没有。为了不再靠人肉 review 发现，这里固化成测试。
 *
 * <p>注意：本测试直接连 dev 库（{@code @ActiveProfiles("dev")}），
 * 用例会临时改 {@code deleted} 标记，并在 finally 中恢复。
 *
 * <p>用随机 HTTP 端口 + 独立的 Dubbo 端口，避免与本机正在运行的
 * auth 实例抢占 8999 / 20880。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"dubbo.protocol.port=28080"})
@ActiveProfiles("dev")
class AuthCustomSqlRegressionTest {

    /** 匹配 `from xxx.yyy` / `join xxx.yyy`，即「库名.表名」这种写死库名的写法 */
    private static final Pattern SCHEMA_QUALIFIED_TABLE =
            Pattern.compile("(?i)\\b(?:from|join)\\s+`?([A-Za-z_][A-Za-z0-9_]*)`?\\s*\\.");

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("P0-2：自定义 SQL 不得写死库名（否则 local / prod 必然报表不存在）")
    void customSqlMustNotHardcodeSchemaName() throws IOException {
        // 直接校验 mapper XML 源文件：MP 自动生成的语句含 <foreach>，
        // 用空参数取 BoundSql 会 NPE，而自定义 XML 语句才是本测试要守的对象。
        // 这些语句的真实执行结果由本类的其余用例覆盖。
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/*.xml");
        assertTrue(resources.length > 0, "没有扫描到任何 mapper XML，测试本身失效");

        for (Resource resource : resources) {
            String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            // 去掉 XML 注释，避免注释里的示例文字被当成 SQL
            String sql = xml.replaceAll("(?s)<!--.*?-->", "");

            Matcher matcher = SCHEMA_QUALIFIED_TABLE.matcher(sql);
            assertFalse(matcher.find(),
                    () -> resource.getFilename() + " 里写死了库名（" + matcher.group(1) + "），"
                            + "应改用裸表名，让数据源决定库");
        }
    }

    @Test
    @DisplayName("P0-3：逻辑删除的用户不能被 getUserByUsername 命中")
    void logicallyDeletedUserMustNotBeFound() {
        SysUser user = sysUserMapper.getUserByUsername("xiaolin").orElse(null);
        assertNotNull(user, "dev 库应存在测试用户 xiaolin");

        jdbcTemplate.update("update sys_user set deleted = 1 where id = ?", user.getId());
        try {
            assertNull(sysUserMapper.getUserByUsername("xiaolin").orElse(null),
                    "已逻辑删除的用户仍被查出，deleted = 0 过滤没生效");
        } finally {
            jdbcTemplate.update("update sys_user set deleted = 0 where id = ?", user.getId());
        }

        assertNotNull(sysUserMapper.getUserByUsername("xiaolin").orElse(null), "恢复 deleted = 0 后应能查回");
    }

    @Test
    @DisplayName("P0-3：用户-角色关联被逻辑删除后，listUsersByRoleId 不应再返回该用户")
    void logicallyDeletedUserRoleMustBeExcluded() {
        SysUser user = sysUserMapper.getUserByUsername("xiaolin").orElse(null);
        assertNotNull(user);

        Long relationId = jdbcTemplate.queryForObject(
                "select id from sys_user_role where sys_user_id = ? and deleted = 0 limit 1",
                Long.class, user.getId());
        assertNotNull(relationId, "dev 库应存在 xiaolin 的用户-角色关联");

        jdbcTemplate.update("update sys_user_role set deleted = 1 where id = ?", relationId);
        try {
            List<SysUser> users = sysUserMapper.listUsersByRoleId(
                    jdbcTemplate.queryForObject(
                            "select sys_role_id from sys_user_role where id = ?", Long.class, relationId));
            assertTrue(users.stream().noneMatch(u -> user.getId().equals(u.getId())),
                    "关联关系已逻辑删除，用户仍出现在 listUsersByRoleId 结果中");
        } finally {
            jdbcTemplate.update("update sys_user_role set deleted = 0 where id = ?", relationId);
        }
    }

    @Test
    @DisplayName("P0-3：角色-权限关联被逻辑删除后，listRolesByPermId 不应再返回该角色")
    void logicallyDeletedRolePermissionMustBeExcluded() {
        Long permId = jdbcTemplate.queryForObject(
                "select sys_permission_id from sys_role_permission where deleted = 0 limit 1", Long.class);
        assertNotNull(permId, "dev 库应存在角色-权限关联");

        List<SysRole> before = sysRoleMapper.listRolesByPermId(permId);
        assertFalse(before.isEmpty(), "listRolesByPermId 应返回拥有该权限的角色（该 select 此前是空的）");

        Long relationId = jdbcTemplate.queryForObject(
                "select id from sys_role_permission where sys_permission_id = ? and deleted = 0 limit 1",
                Long.class, permId);
        jdbcTemplate.update("update sys_role_permission set deleted = 1 where id = ?", relationId);
        try {
            List<SysRole> after = sysRoleMapper.listRolesByPermId(permId);
            assertTrue(after.size() < before.size(),
                    "关联关系已逻辑删除，listRolesByPermId 的结果数应减少");
        } finally {
            jdbcTemplate.update("update sys_role_permission set deleted = 0 where id = ?", relationId);
        }
    }

    @Test
    @DisplayName("权限查询语句能正常执行（防止补 deleted 时把 SQL 写坏）")
    void permissionQueriesMustExecute() {
        SysUser user = sysUserMapper.getUserByUsername("xiaolin").orElse(null);
        assertNotNull(user);

        assertNotNull(sysUserMapper.listUsersByRoleId(
                jdbcTemplate.queryForObject(
                        "select sys_role_id from sys_user_role where sys_user_id = ? limit 1",
                        Long.class, user.getId())));
        assertNotNull(sysUserMapper.listUsersByPermId(
                jdbcTemplate.queryForObject(
                        "select sys_permission_id from sys_role_permission limit 1", Long.class)));
        assertNotNull(sysUserMapper.listUsersWithRoleByPermId(
                jdbcTemplate.queryForObject(
                        "select sys_permission_id from sys_role_permission limit 1", Long.class)));
        assertNotNull(sysRoleMapper.listRolesByUserId(user.getId()));
        assertNotNull(sysPermissionMapper.listPermsByUserId(user.getId()));
        assertNotNull(sysPermissionMapper.listPermsWithRoleByUserId(user.getId()));
        assertNotNull(sysPermissionMapper.listPermsByUsername("xiaolin"));
        assertNotNull(sysPermissionMapper.listPermsWithRoleByUsername("xiaolin"));
        assertNotNull(sysPermissionMapper.listPermsByRoleId(
                jdbcTemplate.queryForObject(
                        "select sys_role_id from sys_role_permission limit 1", Long.class)));
    }
}
