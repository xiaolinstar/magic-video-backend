package cn.xiaolin.has.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转码任务消息。JSON 字段名与 {@code contracts/asyncapi/transcode.v1.yaml} 以及 multimedia 侧 DTO 一致：
 * {@code sourceUrl}、{@code videoId}、{@code resourceKey}。
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TranscodeMessage {

    /** 源视频访问地址（mp4），供 ffmpeg 拉取 */
    private String sourceUrl;

    /**
     * 关联的影视 id，对应 {@code video_source.video_id}。
     * 转码完成后回写 core，落库时作为播放源归属的影视。
     */
    private Long videoId;

    /**
     * 资源标识（源视频对象名，去掉后缀）。
     * 同时作为转码幂等键：同一资源重复投递时不会重复转码、不会覆盖已有产物。
     */
    private String resourceKey;
}
