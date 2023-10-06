# CREATE DATABASE IF NOT EXISTS `dev_auth`;
# USE `dev_auth`;
CREATE DATABASE IF NOT EXISTS `prod_auth`;
USE `prod_auth`;

# 视频表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `username`           VARCHAR(255) COMMENT '用户名',
    `email`              VARCHAR(255) COMMENT '邮箱',
    `phone`              VARCHAR(255) COMMENT '手机号',
    `password`           VARCHAR(255) COMMENT '密码',
    `admission`          TINYINT(1)          DEFAULT false COMMENT '准入权限',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE INDEX `idx_username` (username),
    UNIQUE INDEX `idx_email` (email),
    UNIQUE INDEX `idx_phone` (phone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户';


DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `name`               VARCHAR(255) COMMENT '角色名',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_name` (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统角色';

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键;雪花算法生成，Jackson序列化时转string',
    `name`               VARCHAR(255) COMMENT '权限名',
    `description`         VARCHAR(255) COMMENT '权限描述',
    `view`                TINYINT(1)          DEFAULT true COMMENT '查看',
    `modify`              TINYINT(1)          DEFAULT false COMMENT '修改',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE INDEX `idx_name` (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统权限';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `sys_user_id`        BIGINT     NOT NULL COMMENT '用户Id',
    `sys_role_id`        BIGINT     NOT NULL COMMENT '角色Id',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_sys_user_id` (sys_user_id),
    INDEX `idx_sys_role_id` (sys_role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户角色关联表';

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键g',
    `sys_role_id`        BIGINT     NOT NULL COMMENT '系统角色Id',
    `sys_permission_id`  BIGINT     NOT NULL COMMENT '系统权限Id',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_sys_role_id` (sys_role_id),
    INDEX `idx_sys_permission_id` (sys_permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统角色权限关联表';

DROP TABLE IF EXISTS `sys_user_permission`;
CREATE TABLE `sys_user_permission`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键;雪花算法生成，Jackson序列化时转string',
    `sys_user_id`        BIGINT     NOT NULL COMMENT '用户Id',
    `sys_permission_id`  BIGINT     NOT NULL COMMENT '权限Id',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_sys_user_id` (sys_user_id),
    INDEX `idx_sys_permission_id` (sys_permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户权限关联表';
