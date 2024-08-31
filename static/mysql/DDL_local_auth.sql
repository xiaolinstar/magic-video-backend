CREATE DATABASE IF NOT EXISTS `dev_auth`;
USE `dev_auth`;

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

insert into `sys_user` value (1, 'li', 'li@qq.com',
                              '18811019971', '123456li', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (2, 'tom', 'tom@gmail.com',
                              '18811019972', '123456tom', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (3, 'mike', 'mike@163.com',
                              '18811019973', '123456mike', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (4, 'xing', 'xing@qq.com',
                              '18811019974', '123456xing', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (5, 'zhang', 'zhang@qq.com',
                              '18811019975', '123456zhang', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (6, 'xingxiaolin', 'xlxing@bupt.edu.cn',
                              '18811019976', '123456xxl', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (7, 'rick', 'rick@qq.com',
                              '18811019977', '123456rick', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (8, 'morty', 'morty@qq.com',
                              '18811019978', '123456morty', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (9, 'lucky', 'lucky@qq.com',
                              '18811019979', '123456lucky', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);
insert into `sys_user` value (10, 'fun', 'fun@qq.com',
                              '18811019910', '123456fun', true, 0, false,
                              '2023-08-13 11:40:03', '2023-08-13 11:40:03', 6, 6);

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

insert into `sys_role` value(1, 'common', 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role` value(2, 'vip-1', 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role` value(3, 'vip-2', 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role` value(4, 'admin', 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role` value(5, 'super-admin', 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);


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

insert into `sys_permission` value(1, 'admin.add', '管理员-新增', true, true, 1, false,
                                   '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_permission` value(2, 'admin.query', '管理员-查询', true, true, 1, false,
                                   '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_permission` value(3, 'admin.delete', '管理员-删除', true, true, 1, false,
                                   '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_permission` value(4, 'admin.update', '管理员-更新', true, true, 1, false,
                                   '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_permission` value(5, 'common.query', '普通用户-查询', true, true, 1, false,
                                   '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);


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

insert into `sys_user_role` value(1, 6, 1, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_user_role` value(2, 6, 4, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_user_role` value(3, 8, 1, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
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

insert into `sys_role_permission` value (1, 1, 5, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role_permission` value (2, 4, 1, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role_permission` value (3, 4, 2, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role_permission` value (4, 4, 3, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);
insert into `sys_role_permission` value (5, 4, 4, 0, false, '2023-9-23 08:10:26', '2023-9-23 08:10:26', 6, 6);


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
