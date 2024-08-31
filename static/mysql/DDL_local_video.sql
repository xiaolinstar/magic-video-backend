# USE dev_magic_video;
CREATE DATABASE IF NOT EXISTS `dev_magic_video`;
USE dev_magic_video;

# 视频表
DROP TABLE IF EXISTS video;
CREATE TABLE video
(
    `id`                 BIGINT       NOT NULL COMMENT '主键id',
    `name`               VARCHAR(90)  NOT NULL DEFAULT '未命名' COMMENT '中文名称',
    `icon`               VARCHAR(255) NOT NULL COMMENT '封面，使用base64存储',
    `rating`             DECIMAL(3, 2)         DEFAULT 0.0 COMMENT '评分',
    `year`               INT          NOT NULL COMMENT '年份',
    `release_date`       DATETIME COMMENT '发布日期',
    `alias`              VARCHAR(255) COMMENT '别名',
    `description`        VARCHAR(900) COMMENT '剧情摘要',
    `resource_id`        BIGINT COMMENT '资源id',
    `deleted`            TINYINT(1)   NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT COMMENT '乐观锁',
    `create_time`        DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME     NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT       NOT NULL COMMENT '创建人id',
    `updated_by_user_id` BIGINT       NOT NULL COMMENT '更新人id',
    PRIMARY KEY (id),
    INDEX `idx_year` (year),
    INDEX `idx_resource_id` (resource_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频';


# 视频分类
DROP TABLE IF EXISTS category;
CREATE TABLE category
(
    `id`                 BIGINT      NOT NULL COMMENT '主键',
    `name`               VARCHAR(90) NOT NULL COMMENT '',
    `deleted`            TINYINT(1)  NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                  DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME    NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME    NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT      NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT      NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频分类';


# 视频分类关联关系表
DROP TABLE IF EXISTS video_category;
CREATE TABLE video_category
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `category_id`        BIGINT     NOT NULL COMMENT '视频类别id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_category_id` (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频分类关联关系表';

# 国家和地区
DROP TABLE IF EXISTS district;
CREATE TABLE district
(
    `id`                 BIGINT      NOT NULL COMMENT '主键',
    `name`               VARCHAR(90) NOT NULL COMMENT '名称',
    `capital`            VARCHAR(90) NOT NULL COMMENT '首都',
    `deleted`            TINYINT(1)  NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                  DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME    NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME    NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT      NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT      NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '国家和地区';

# 视频地区关联关系表
DROP TABLE IF EXISTS video_district;
CREATE TABLE video_district
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `district_id`        BIGINT     NOT NULL COMMENT '地区id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_district_id` (district_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频地区关联关系表';

# 标签
DROP TABLE IF EXISTS tag;
CREATE TABLE tag
(
    `id`                 BIGINT      NOT NULL COMMENT '主键',
    `name`               VARCHAR(90) NOT NULL COMMENT '名称',
    `deleted`            TINYINT(1)  NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                  DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME    NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME    NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT      NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT      NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '标签';

# 视频标签关联关系表
DROP TABLE IF EXISTS video_tag;
CREATE TABLE video_tag
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `tag_id`             BIGINT     NOT NULL COMMENT '标签id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_tag_id` (tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频标签关联关系表';

# 视频导演
DROP TABLE IF EXISTS video_director;
CREATE TABLE video_director
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `director_id`        BIGINT COMMENT '导演id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_director_id` (director_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频导演';

# 视频
DROP TABLE IF EXISTS video_scripter;
CREATE TABLE video_scripter
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `scripter_id`        BIGINT     NOT NULL COMMENT '编剧id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_scripter_id` (scripter_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频编剧';

# 视频演员
DROP TABLE IF EXISTS video_actor;
CREATE TABLE video_actor
(
    `id`                 BIGINT     NOT NULL COMMENT '主键',
    `video_id`           BIGINT     NOT NULL COMMENT '视频id',
    `actor_id`           BIGINT     NOT NULL COMMENT '演员id',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                 DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_video_id` (video_id),
    INDEX `idx_actor_id` (actor_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频演员';

# 视频制作人
DROP TABLE IF EXISTS video_maker;
CREATE TABLE video_maker
(
    `id`                 BIGINT       NOT NULL COMMENT '主键',
    `name`               VARCHAR(90)  NOT NULL COMMENT '名称',
    `gender`             VARCHAR(90) COMMENT '性别',
    `constellation`      VARCHAR(90) COMMENT '星座',
    `icon`               VARCHAR(255) NOT NULL COMMENT '封面图',
    `birthday`           DATETIME COMMENT '生日',
    `introduction`       VARCHAR(900) COMMENT '介绍',
    `photo_path`         VARCHAR(255) COMMENT '个人照片集目录',
    `is_actor`           TINYINT(1)   NOT NULL DEFAULT false COMMENT '是否是演员',
    `is_scripter`        TINYINT(1)   NOT NULL DEFAULT false COMMENT '是否是编剧',
    `is_director`        TINYINT(1)   NOT NULL DEFAULT false COMMENT '是否是导演',
    `deleted`            TINYINT(1)   NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`           INT                   DEFAULT 0 COMMENT '乐观锁',
    `create_time`        DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME     NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT       NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT       NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频制作人';

# 奖项
DROP TABLE IF EXISTS award;
CREATE TABLE award
(
    `id`                              BIGINT       NOT NULL COMMENT '主键',
    `name`                            VARCHAR(90)  NOT NULL COMMENT '奖项名称',
    `icon`                            VARCHAR(255) NOT NULL COMMENT '封面图',
    `year`                            INT          NOT NULL COMMENT '年度',
    `country`                         VARCHAR(255) NOT NULL COMMENT '国家或地区',
    `best_director_id`                BIGINT COMMENT '最佳导演',
    `best_director_nomination_id`     BIGINT COMMENT '最佳导演提名',
    `best_male_actor_id`              BIGINT COMMENT '最佳男主角',
    `best_female_actor_id`            BIGINT COMMENT '最佳女主角',
    `best_male_actor_nomination_id`   BIGINT COMMENT '最佳男主角提名',
    `best_female_actor_nomination_id` BIGINT COMMENT '最佳女主角提名',
    `best_scripter_id`                BIGINT COMMENT '最佳编剧',
    `best_scripter_nomination_id`     BIGINT COMMENT '最佳编剧提名',
    `best_new_actor_id`               BIGINT COMMENT '最佳新人',
    `deleted`                         TINYINT(1)   NOT NULL DEFAULT false COMMENT '逻辑删除',
    `revision`                        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `create_time`                     DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`                     DATETIME     NOT NULL COMMENT '更新时间',
    `created_by_user_id`              BIGINT       NOT NULL COMMENT '创建人',
    `updated_by_user_id`              BIGINT       NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX `idx_year` (year)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '奖项';


# 资源
DROP TABLE IF EXISTS resource;
CREATE TABLE resource
(
    `id`                 BIGINT     NOT NULL COMMENT '主键;雪花算法生成，Jackson序列化时转string',
    `name`               VARCHAR(255) COMMENT '资源名称',
    `md5`                VARCHAR(255) COMMENT '摘要算法md5值;判断数据库中是否已经存在，避免重复上传',
    `mp4`                VARCHAR(255) COMMENT 'MP4资源地址',
    `m3u8`               VARCHAR(255) COMMENT 'HLS资源',
    `mpd`                VARCHAR(255) COMMENT 'DASH资源',
    `avatar`             VARCHAR(255) COMMENT '封面图',
    `title`              VARCHAR(255) COMMENT '标题',
    `description`        VARCHAR(255) COMMENT '标题',
    `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
    `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
    `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
    `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
    `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '视频资源';

INSERT INTO `resource` VALUES (1693269599677710338,'关于我和鬼变成家人的那件事-开头剪辑',NULL,NULL,'video/396bbfb6-0e84-4fcd-a2b3-eea5e797b58a/720p.m3u8',NULL,'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/prettygirl.tiff','许光汉健身房激情撩汉','这是一个描述，但是我不知道写些什么',0,0,'2023-08-20 22:34:45','2023-08-20 22:34:45',0,0),(1693270668365398018,'关于我和鬼变成家人的那件事',NULL,NULL,'video/813a2632-df0e-42cf-a8b3-129af4fdc9ea/main.m3u8',NULL,'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/fiezao.tiff','许光汉为爱当0，炎亚纶本色出演','这是一个描述，但是我不知道写些什么',0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),(1695811501761363970,'性爱自修室3开头剪辑.mp4',NULL,NULL,'video/0ce3704b-b1ed-4c7d-a63c-960bab5267f5/1080p.m3u8',NULL,'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/xiaohei.tiff','性爱自修室3丝滑片头欣赏','这是一个视频默认描述',0,0,'2023-08-27 22:54:42','2023-08-27 22:54:42',0,0),(1695813115452719105,'华尔街之狼-乔丹的启蒙老师.mp4',NULL,NULL,'video/c1b2d0ff-561c-468f-883b-d053ce083339/1080p.m3u8',NULL,'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/teacher.tiff','华尔街之狼','乔丹初入华尔街的的精神导师',0,0,'2023-08-27 23:14:55','2023-08-27 23:14:55',0,0),(1705467938741551105,'性爱自修室4-片头',NULL,NULL,'video/46ab56bb-f49e-4f0d-901c-700da442d5e4/1080p.m3u8',NULL,'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/give-me-yours.tiff','我发了，你呢','梅芙给Otis发胸照，Otis不知道如何回复',0,0,'2023-09-23 14:31:41','2023-09-23 14:31:41',0,0);
