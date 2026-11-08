package cn.xiaolin.multimedia.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源元数据消息。JSON 字段名与契约一致：{@code id}、{@code md5}、{@code mp4}、{@code m3u8}、{@code mpd}。
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResourceMessage {
    /** 资源Id */
    private Long id;
    /** 资源摘要 */
    private String md5;
    /** 视频资源 */
    private String mp4;
    /** HLS资源 */
    private String m3u8;
    /** DASH资源 */
    private String mpd;
}
