# 归档的 DDL

现行建表以 `static/mysql/{dev,local,prod}/` 为准。三套环境的表集合相同：

- auth：`sys_user`、`sys_role`、`sys_permission`、`sys_user_role`、`sys_role_permission`、`sys_user_permission`
- video：`collection`、`season`、`video`、`video_source`、`category`、`video_category`、`district`、`video_district`、`tag`、`video_tag`、`video_director`、`video_scripter`、`video_actor`、`video_maker`、`award`

库名和种子数据仍按环境区分。`sys_user.admission` 的列默认值在 dev 是 `true`，在 local/prod 是 `false`；现有 INSERT 都显式写成 `true`。

## 本目录

`DDL_video_backup.sql` 是旧版视频库草稿，里面有已废弃的 `videoResource`（驼峰表名，没有对应实体）。播放地址由 `video_source` 承担。不要把本目录挂到 `MYSQL_INIT`。
