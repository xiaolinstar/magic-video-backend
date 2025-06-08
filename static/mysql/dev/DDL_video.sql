# USE dev_magic_video;
CREATE DATABASE IF NOT EXISTS `dev_magic_video`;
USE dev_magic_video;

DROP TABLE IF EXISTS collection;
CREATE TABLE collection
(
    `id` BIGINT NOT NULL   COMMENT '主键;雪花算法生成，Jackson序列化时转string' ,
    `title` VARCHAR(255) NOT NULL DEFAULT '集合未命名标题'  COMMENT '标题' ,
    `type` VARCHAR(32) NOT NULL   COMMENT '类型' ,
    `description` VARCHAR(900) NOT NULL   COMMENT '描述' ,
    `cover_image` VARCHAR(255) NOT NULL   COMMENT '封面' ,
    `revision` INT   DEFAULT 0 COMMENT '乐观锁' ,
    `deleted` TINYINT(1) NOT NULL  DEFAULT false COMMENT '逻辑删除' ,
    `create_time` DATETIME NOT NULL   COMMENT '创建时间' ,
    `update_time` DATETIME NOT NULL   COMMENT '更新时间' ,
    `created_by_user_id` BIGINT NOT NULL   COMMENT '创建人' ,
    `updated_by_user_id` BIGINT NOT NULL   COMMENT '更新人' ,
    PRIMARY KEY (id)
)  ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4 COMMENT = '视频合集';

insert into collection values   (1001, '疯狂的麦克斯系列', 'movie-series', '影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazy-max.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (3001, '寂静之海', 'tv-series', '韩国科幻惊悚剧集，讲述2075年地球水资源危机背景下的月球探险故事', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);

DROP TABLE IF EXISTS season;
CREATE TABLE season
(
   `id` BIGINT NOT NULL   COMMENT '主键;雪花算法生成，Jackson序列化时转string' ,
   `collection_id` BIGINT NOT NULL   COMMENT '集合id' ,
   `season_number` INT NOT NULL   COMMENT '季号' ,
   `title` VARCHAR(255)    COMMENT '标题' ,
   `description` VARCHAR(900)    COMMENT '描述' ,
   `cover_image` VARCHAR(255)    COMMENT '封面url' ,
   `release_date` DATETIME    COMMENT '发布日期' ,
   `revision` INT   DEFAULT 0 COMMENT '乐观锁' ,
   `deleted` TINYINT(1) NOT NULL  DEFAULT false COMMENT '逻辑删除' ,
   `create_time` DATETIME NOT NULL   COMMENT '创建时间' ,
   `update_time` DATETIME NOT NULL   COMMENT '更新时间' ,
   `created_by_user_id` BIGINT NOT NULL   COMMENT '创建人' ,
   `updated_by_user_id` BIGINT NOT NULL   COMMENT '更新人' ,
   PRIMARY KEY (id)
)  ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4 COMMENT = '季';

insert into season values   (20011, 2001, 1, '性爱自修室 第1季', '奥蒂斯的性教育咨询之路开始', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg', '2019-01-11', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                            (30011, 3001, 1, '寂静之海 第1季', '月球基地的神秘事件调查', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/silent-sea.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);

DROP TABLE IF EXISTS video;
CREATE TABLE video
(
  `id` BIGINT NOT NULL   COMMENT '主键id' ,
  `title` VARCHAR(90) NOT NULL  DEFAULT '未命名' COMMENT '主标题' ,
  `original_title` VARCHAR(90)    COMMENT '原标题' ,
  `description` VARCHAR(900) NOT NULL  DEFAULT '视频描述待补充...' COMMENT '描述' ,
  `cover_image` VARCHAR(255) NOT NULL   COMMENT '封面图url' ,
  `type` VARCHAR(32) NOT NULL DEFAULT 'movie' COMMENT '类型' ,
  `release_date` DATETIME    COMMENT '发布日期' ,
  `duration` INT NOT NULL  DEFAULT 0 COMMENT '时长（秒）' ,
  `rating` DECIMAL(3,2)   DEFAULT 0.0 COMMENT '评分' ,
  `parent_id` BIGINT    COMMENT '集合或季id' ,
  `video_order` INT NOT NULL  DEFAULT 0 COMMENT '集数或部（排序）',
  `deleted` TINYINT(1) NOT NULL  DEFAULT false COMMENT '逻辑删除' ,
  `revision` INT   DEFAULT 0 COMMENT '乐观锁' ,
  `create_time` DATETIME NOT NULL   COMMENT '创建时间' ,
  `update_time` DATETIME NOT NULL   COMMENT '更新时间' ,
  `created_by_user_id` BIGINT NOT NULL   COMMENT '创建人id' ,
  `updated_by_user_id` BIGINT NOT NULL   COMMENT '更新人id' ,
  PRIMARY KEY (id)
)  ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4 COMMENT = '视频';

insert into video values (112342, '疯狂的麦克斯：狂暴女神', null, '影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazy-max.jpg', 'movie', '2024-05-24', 8400, 8.4, 1001, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (300111, '寂静之海 第1集', null, '在未来，地球资源枯竭，人类面临生存危机。韩国政府决定重启“寂静之海”计划，派遣一支精英团队前往月球上废弃的赫密斯基地。团队成员包括队长韩太锡（郑雨盛饰）、生物学家宋智安（韩孝周饰）、工程师尹泰宇（李准饰）等。他们乘坐宇宙飞船抵达月球，发现基地已被废弃多年，但内部设施仍然完好。团队的主要任务是回收一种名为“露西”的神秘植物样本，这种植物被认为能在地球上种植并解决粮食危机。然而，基地内隐藏着许多未知的危险，团队成员们很快意识到他们的任务并不简单。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 30011, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (300112, '寂静之海 第2集', null, '团队在赫密斯基地展开调查，发现基地内部异常安静，仿佛隐藏着某种秘密。在探索过程中，他们接收到一段神秘的信号，内容是一段模糊的录音，似乎来自多年前的基地人员。录音中提到“不要打开那个门”，这让团队成员感到困惑和不安。与此同时，基地的电力系统出现故障，导致部分区域停电，团队不得不依靠备用电源维持生存。在修复电力系统的过程中，宋智安发现“露西”植物的生长环境异常，似乎受到了某种未知因素的影响。团队成员之间的信任开始出现裂痕，每个人都在猜测基地中到底隐藏着什么。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 30011, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (200111, '性爱自修室 Otis Ruby', null, '欧提思（阿萨·巴特菲尔德 Asa Butterfield 饰）是一位非常平凡的高中生，他既没有特别聪明，也没有特别英俊，要说他和同龄人唯一的不同，那就是他拥有一位研究性心理学的母亲简（吉莲·安德森 Gillian Anderson 饰）。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg', 'episode', '2024-05-24', 3100, null, 20011, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);


DROP TABLE IF EXISTS video_source;
CREATE TABLE video_source
(
    `id` BIGINT NOT NULL   COMMENT '主键;雪花算法生成，Jackson序列化时转string' ,
    `video_id` BIGINT NOT NULL   COMMENT '视频id' ,
    `src` VARCHAR(255) NOT NULL   COMMENT '资源地址url' ,
    `type` VARCHAR(32) NOT NULL DEFAULT 'dash'  COMMENT '资源类型' ,
    `resolution` INT NOT NULL DEFAULT 1080 COMMENT '分辨率' ,
    `bitrate` INT    COMMENT '码率kbps' ,
    `revision` INT   DEFAULT 0 COMMENT '乐观锁' ,
    `deleted` TINYINT(1) NOT NULL  DEFAULT false COMMENT '逻辑删除' ,
    `create_time` DATETIME NOT NULL   COMMENT '创建时间' ,
    `update_time` DATETIME NOT NULL   COMMENT '更新时间' ,
    `created_by_user_id` BIGINT NOT NULL   COMMENT '创建人' ,
    `updated_by_user_id` BIGINT NOT NULL   COMMENT '更新人' ,
    PRIMARY KEY (id)
)  ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4 COMMENT = '视频源';


# # 视频表
# DROP TABLE IF EXISTS video;
# CREATE TABLE video
# (
#     `id`                 BIGINT       NOT NULL COMMENT '主键id',
#     `name`               VARCHAR(90)  NOT NULL DEFAULT '未命名' COMMENT '中文名称',
#     `icon`               VARCHAR(255) NOT NULL COMMENT '封面，使用base64存储',
#     `rating`             DECIMAL(3, 2)         DEFAULT 0.0 COMMENT '评分',
#     `year`               INT          NOT NULL COMMENT '年份',
#     `release_date`       DATETIME COMMENT '发布日期',
#     `alias`              VARCHAR(255) COMMENT '别名',
#     `description`        VARCHAR(900) COMMENT '剧情摘要',
#     `resource_id`        BIGINT COMMENT '资源id',
#     `deleted`            TINYINT(1)   NOT NULL DEFAULT false COMMENT '逻辑删除',
#     `revision`           INT COMMENT '乐观锁',
#     `create_time`        DATETIME     NOT NULL COMMENT '创建时间',
#     `update_time`        DATETIME     NOT NULL COMMENT '更新时间',
#     `created_by_user_id` BIGINT       NOT NULL COMMENT '创建人id',
#     `updated_by_user_id` BIGINT       NOT NULL COMMENT '更新人id',
#     PRIMARY KEY (id),
#     INDEX `idx_year` (year),
#     INDEX `idx_resource_id` (resource_id)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8mb4 COMMENT = '视频';




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

INSERT INTO category values ('1', '影视剧集', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1'),
                            ('2', '综艺娱乐', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1'),
                            ('3', '纪录片', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1'),
                            ('4', '动画动漫', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1'),
                            ('5', '直播切片', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1'),
                            ('6', '知识教育', false, 0, '2025-04-07 15:47:25', '2025-04-07 15:47:25', '1', '1');

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
# DROP TABLE IF EXISTS videoResource;
# CREATE TABLE videoResource
# (
#     `id`                 BIGINT     NOT NULL COMMENT '主键;雪花算法生成，Jackson序列化时转string',
#     `name`               VARCHAR(255) COMMENT '资源名称',
#     `md5`                VARCHAR(255) COMMENT '摘要算法md5值;判断数据库中是否已经存在，避免重复上传',
#     `mp4`                VARCHAR(2048) COMMENT 'MP4资源地址',
#     `m3u8`               VARCHAR(2048) COMMENT 'HLS资源',
#     `mpd`                VARCHAR(2048) COMMENT 'DASH资源',
#     `avatar`             VARCHAR(2048) COMMENT '封面图',
#     `title`              VARCHAR(255) COMMENT '标题',
#     `description`        VARCHAR(2048) COMMENT '标题',
#     `revision`           INT        NOT NULL DEFAULT 0 COMMENT '乐观锁',
#     `deleted`            TINYINT(1) NOT NULL DEFAULT false COMMENT '逻辑删除',
#     `create_time`        DATETIME   NOT NULL COMMENT '创建时间',
#     `update_time`        DATETIME   NOT NULL COMMENT '更新时间',
#     `created_by_user_id` BIGINT     NOT NULL COMMENT '创建人',
#     `updated_by_user_id` BIGINT     NOT NULL COMMENT '更新人',
#     PRIMARY KEY (id)
# ) ENGINE = InnoDB
#   DEFAULT CHARSET = utf8mb4 COMMENT = '视频资源';
#
# INSERT INTO `videoResource` VALUES (0,' 疯狂麦克斯-狂暴女神','Md5-Crazy-Max',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/Md5-Crazy-Max/Md5-Crazy-Max.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazy-max.jpg','疯狂麦克斯之狂暴女神','影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。年轻的弗瑞奥萨从原本的家园被掠走，落入军阀狄门特斯（克里斯·海姆斯沃斯 Chris Hemsworth 饰）领导的帮派手中，在穿过荒原时，他们来到不死老乔（拉黑·休姆 Lachy H ulme 饰）所掌管的堡垒。在两位暴君争夺统治地位的同时，弗瑞奥萨必须在重重考验中活下来，并想方设法寻找回家的路；她也逐渐成长为利落酷飒的狂暴女神。',0,0,'2023-08-20 22:34:45','2023-08-20 22:34:45',0,0),
#                               (1,'性爱自修室 Otis & Ruby','Md5-Otis-Ruby',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/Md5-Otis-Ruby/Md5-Otis-Ruby.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg','Sex Education: Otis & Ruby','超级好看！虽然各种性贯穿全片，但又是特别的纯情走心，甚至很多地方堪称浪漫。校霸欺负gay，围观的直男说：算了吧，恐同是2008年的流行了。有趣流畅不说教，真实冷静不偏激，内容完全对得起“性教育”的这个片名。',0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
#                               (2,'关于我和鬼变成家人的那件事','Md5-Marry-My-Dead-Body',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/MarryMyDeadBody/main.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/MarryMyDeadBody.jpg','关于我和鬼结婚的那件事，许光汉色诱浴池抓毒','吴明翰（许光汉 饰）是一名脾气耿直的警察，一次在出警中，他偶然捡到了地上的红包，哪知道被牵扯到了一桩冥婚之中，而他要结婚的对象，竟然是名叫毛邦羽（林柏宏 饰）的男人。吴明翰虽然内心一百个不情愿，但很快他就发现，如果不完婚，那么坏运气会一直跟随着他，无奈之下，吴明翰只得在灵堂中和毛邦宇举行了仪式。',0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
#                               (3,' 疯狂麦克斯-狂暴女神','Md5-Crazy-Max',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/Md5-Crazy-Max/Md5-Crazy-Max.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazy-max.jpg','疯狂麦克斯之狂暴女神','影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。年轻的弗瑞奥萨从原本的家园被掠走，落入军阀狄门特斯（克里斯·海姆斯沃斯 Chris Hemsworth 饰）领导的帮派手中，在穿过荒原时，他们来到不死老乔（拉黑·休姆 Lachy H ulme 饰）所掌管的堡垒。在两位暴君争夺统治地位的同时，弗瑞奥萨必须在重重考验中活下来，并想方设法寻找回家的路；她也逐渐成长为利落酷飒的狂暴女神。',0,0,'2023-08-20 22:34:45','2023-08-20 22:34:45',0,0),
#                               (4,'性爱自修室 Otis & Ruby','Md5-Otis-Ruby',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/Md5-Otis-Ruby/Md5-Otis-Ruby.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg','Sex Education: Otis & Ruby','超级好看！虽然各种性贯穿全片，但又是特别的纯情走心，甚至很多地方堪称浪漫。校霸欺负gay，围观的直男说：算了吧，恐同是2008年的流行了。有趣流畅不说教，真实冷静不偏激，内容完全对得起“性教育”的这个片名。',0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
#                               (5,'关于我和鬼变成家人的那件事','Md5-Marry-My-Dead-Body',NULL,NULL,'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/MarryMyDeadBody/main.mpd','https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/MarryMyDeadBody.jpg','关于我和鬼结婚的那件事，许光汉色诱浴池抓毒','吴明翰（许光汉 饰）是一名脾气耿直的警察，一次在出警中，他偶然捡到了地上的红包，哪知道被牵扯到了一桩冥婚之中，而他要结婚的对象，竟然是名叫毛邦羽（林柏宏 饰）的男人。吴明翰虽然内心一百个不情愿，但很快他就发现，如果不完婚，那么坏运气会一直跟随着他，无奈之下，吴明翰只得在灵堂中和毛邦宇举行了仪式。',0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0)
