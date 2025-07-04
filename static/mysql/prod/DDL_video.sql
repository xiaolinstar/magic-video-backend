# USE magic_video;
CREATE DATABASE IF NOT EXISTS `magic_video`;
USE magic_video;

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

insert into collection values   (1001, '疯狂的麦克斯系列电影', 'movie-series', '影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazt-max-v8.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1005, '寂静之海', 'tv-series', '韩国科幻惊悚剧集，讲述2075年地球水资源危机背景下的月球探险故事', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1004, '性爱自修室', 'tv-series', '英国青春喜剧剧集，探讨青少年性教育话题', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1008, '弥留之国的爱丽丝', 'tv-series', '日本科幻悬疑剧集，改编自同名漫画。有栖良平与好友苅部大吉、势川张太误入了一个名为“弥留之国”的神秘世界。在这里，他们必须通过一系列以生命为赌注的生存游戏来获取生存的“签证”，否则将被激光杀死', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1009, '鱿鱼游戏', 'tv-series', '456名生活陷入困境、背负巨额债务的人被神秘组织邀请参加一场神秘的生存游戏。参与者被带到一个与世隔绝的岛屿上，通过一系列看似简单但极其危险的儿童游戏来争夺最终的奖金——456亿韩元。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1010, '我的名字', 'tv-series', '尹智友的父亲是一名警察，在执行任务时被一个神秘的犯罪组织“黑道”杀害。为了查明真相并为父亲报仇，尹智友决定加入这个犯罪组织，从内部寻找杀害父亲的真凶。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (1011, '王国', 'tv-series', '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 0,0,'2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);

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

insert into season values   (10051, 1005, 1, '寂静之海 第1季', '月球基地的神秘事件调查', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/silent-sea.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                            (10081, 1008, 1, '弥留之国的爱丽丝 第1季', '弥留之国的生存游戏', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                            (10091, 1009, 1, '鱿鱼游戏 第1季', '456名生活陷入困境、背负巨额债务的人被神秘组织邀请参加一场神秘的生存游戏。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                            (10101, 1010, 1, '我的名字 第1季', '女主角尹智友（由金多美饰演）为父报仇。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                            (10111, 1011, 1, '王国 第1季', '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', '2021-12-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);
#                             (10092, 1009, 2, '鱿鱼游戏 第2季', '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', '2024-08-24', 0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);

DROP TABLE IF EXISTS video;
CREATE TABLE video
(
  `id` BIGINT NOT NULL   COMMENT '主键id' ,
  `title` VARCHAR(90) NOT NULL  DEFAULT '未命名' COMMENT '主标题' ,
  `original_title` VARCHAR(90)    COMMENT '原标题' ,
  `description` VARCHAR(900) NOT NULL  DEFAULT '视频描述待补充...' COMMENT '描述' ,
  `cover_image` VARCHAR(255) NOT NULL DEFAULT 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kindom.jpg'  COMMENT '封面图url' ,
  `type` VARCHAR(32) NOT NULL DEFAULT 'movie' COMMENT '类型' ,
  `release_date` DATETIME    COMMENT '发布日期' ,
  `duration` INT NOT NULL  DEFAULT 0 COMMENT '时长（秒）' ,
  `rating` DECIMAL(3,2)   DEFAULT 0.0 COMMENT '评分' ,
  `parent_id` BIGINT    COMMENT '集合或季id' ,
  `sort_order` INT NOT NULL  DEFAULT 0 COMMENT '集数或部（排序）',
  `deleted` TINYINT(1) NOT NULL  DEFAULT false COMMENT '逻辑删除' ,
  `revision` INT   DEFAULT 0 COMMENT '乐观锁' ,
  `create_time` DATETIME NOT NULL   COMMENT '创建时间' ,
  `update_time` DATETIME NOT NULL   COMMENT '更新时间' ,
  `created_by_user_id` BIGINT NOT NULL   COMMENT '创建人id' ,
  `updated_by_user_id` BIGINT NOT NULL   COMMENT '更新人id' ,
  PRIMARY KEY (id)
)  ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4 COMMENT = '视频';

insert into video values (10011, '疯狂的麦克斯：狂暴女神', null, '影片讲述了复仇女神弗瑞奥萨（安雅·泰勒-乔伊 Anya Taylor-Joy 饰）惊心动魄的成长史。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/crazt-max-v8.jpg', 'movie', '2024-05-24', 8400, 8.4, 1001, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (10041, '性爱自修室', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg', 'clip', '2024-05-24', 3600, null, 1004, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (10042, 'Sex Education: Otis & Ruby', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/Otis%26Ruby.jpg', 'clip', '2024-05-24', 3120, null, 1004, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100501, '寂静之海 第1集', null, '在未来，地球资源枯竭，人类面临生存危机。韩国政府决定重启“寂静之海”计划，派遣一支精英团队前往月球上废弃的赫密斯基地。团队成员包括队长韩太锡（郑雨盛饰）、生物学家宋智安（韩孝周饰）、工程师尹泰宇（李准饰）等。他们乘坐宇宙飞船抵达月球，发现基地已被废弃多年，但内部设施仍然完好。团队的主要任务是回收一种名为“露西”的神秘植物样本，这种植物被认为能在地球上种植并解决粮食危机。然而，基地内隐藏着许多未知的危险，团队成员们很快意识到他们的任务并不简单。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100502, '寂静之海 第2集', null, '团队在赫密斯基地展开调查，发现基地内部异常安静，仿佛隐藏着某种秘密。在探索过程中，他们接收到一段神秘的信号，内容是一段模糊的录音，似乎来自多年前的基地人员。录音中提到“不要打开那个门”，这让团队成员感到困惑和不安。与此同时，基地的电力系统出现故障，导致部分区域停电，团队不得不依靠备用电源维持生存。在修复电力系统的过程中，宋智安发现“露西”植物的生长环境异常，似乎受到了某种未知因素的影响。团队成员之间的信任开始出现裂痕，每个人都在猜测基地中到底隐藏着什么。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100503, '寂静之海 第3集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 3,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100504, '寂静之海 第4集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 4,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100505, '寂静之海 第5集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 5,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100506, '寂静之海 第6集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 6,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100507, '寂静之海 第7集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 7,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100508, '寂静之海 第8集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/SeaLuna.jpg', 'episode', '2024-05-24', 3600, null, 10051, 8,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100801, '弥留之国的爱丽丝 第1集', null, '有栖良平（山崎贤人 饰）拥有一位非常优秀的哥哥，在哥哥的衬托之下，毫无建树的他显得更加的废柴。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100802, '弥留之国的爱丽丝 第2集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100803, '弥留之国的爱丽丝 第3集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 3,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100804, '弥留之国的爱丽丝 第4集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 4,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100805, '弥留之国的爱丽丝 第5集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 5,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100806, '弥留之国的爱丽丝 第6集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 6,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100807, '弥留之国的爱丽丝 第7集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 7,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100808, '弥留之国的爱丽丝 第8集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/alice-poke.jpg', 'episode', '2024-05-24', 3000, 8.4, 10081, 8,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100901, '鱿鱼游戏 第1集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100902, '鱿鱼游戏 第2集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3100, 8.4, 10091, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100903, '鱿鱼游戏 第3集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 3,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100904, '鱿鱼游戏 第4集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 4,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100905, '鱿鱼游戏 第5集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 5,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100906, '鱿鱼游戏 第6集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 6,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100907, '鱿鱼游戏 第7集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 7,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100908, '鱿鱼游戏 第8集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 8,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (100909, '鱿鱼游戏 第9集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/squid-game.jpg', 'episode', '2024-05-24', 3000, 8.4, 10091, 9,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                         (101001, '我的名字 第1集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 7.9, 10101, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101002, '我的名字 第2集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 7.9, 10101, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101003, '我的名字 第3集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 7.9, 10101, 3,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101004, '我的名字 第4集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 8.4, 10101, 4,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101005, '我的名字 第5集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 8.4, 10101, 5,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101006, '我的名字 第6集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 8.4, 10101, 6,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101007, '我的名字 第7集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 8.4, 10101, 7,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101008, '我的名字 第8集', null, '待补充...', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/my-name.jpg', 'episode', '2024-05-24', 3000, 8.4, 10101, 8,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                         (101101, '王国 第1集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 1,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101102, '王国 第2集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 2,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101103, '王国 第3集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 3,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101104, '王国 第4集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 4,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101105, '王国 第5集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 5,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                         (101106, '王国 第6集', null, '王室内部的权力斗争与一场突如其来的丧尸瘟疫之间的交织。', 'https://vod-images-xiaolin.oss-cn-beijing.aliyuncs.com/kingdom.jpg', 'episode', '2024-05-24', 3000, 8.4, 10111, 6,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);





    DROP TABLE IF EXISTS video_source;
CREATE TABLE video_source
(
    `id` BIGINT NOT NULL   COMMENT '主键;雪花算法生成，Jackson序列化时转string' ,
    `video_id` BIGINT NOT NULL   COMMENT '视频id' ,
    `src` VARCHAR(255) NOT NULL   COMMENT '资源地址url' ,
    `type` VARCHAR(32) NOT NULL DEFAULT 'hls'  COMMENT '资源类型' ,
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

insert into video_source values (10011001, 10011, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/crazy-max/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (10041001, 10041, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/Md5-Otis-Ruby/Md5-Otis-Ruby.mpd', 'dash', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                                # 寂静之海
                                (100501001, 100501, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E01/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100502001, 100502, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E02/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100503001, 100503, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E03/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100504001, 100504, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E04/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100505001, 100505, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E05/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100506001, 100506, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E06/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100507001, 100507, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E07/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100508001, 100508, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/the-silent-sea/E08/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                                # 弥留之国的爱丽丝
                                (100801001, 100801, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E01/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100802001, 100802, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E02/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100803001, 100803, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E03/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100804001, 100804, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E04/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100805001, 100805, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E05/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100806001, 100806, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E06/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100807001, 100807, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E07/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100808001, 100808, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/alice-in-borderland/S01/E08/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                                # 鱿鱼游戏 第一季
                                (100901001, 100901, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E01/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100902001, 100902, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E02/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100903001, 100903, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E03/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100904001, 100904, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E04/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100905001, 100905, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E05/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100906001, 100906, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E06/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100907001, 100907, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E07/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100908001, 100908, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E08/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (100909001, 100909, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/squid-game/S01/E09/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                                # 我的名字
                                (101001001, 101001, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E01/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101002001, 101002, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E02/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101003001, 101003, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E03/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101004001, 101004, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E04/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101005001, 101005, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E05/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101006001, 101006, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E06/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101007001, 101007, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E07/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101008001, 101008, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/my-name/E08/hls/main.m3u8', 'hls', 720, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),

                                # 王国
                                (101101001, 101101, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E01/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101102001, 101102, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E02/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101103001, 101103, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E03/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101104001, 101104, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E04/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101105001, 101105, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E05/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0),
                                (101106001, 101106, 'https://magic-video-bucket.oss-cn-nanjing.aliyuncs.com/kingdom/S01/E06/hls/main.m3u8', 'hls', 1080, null,0,0, '2023-08-21 01:39:54','2023-08-21 01:39:54',0,0);
