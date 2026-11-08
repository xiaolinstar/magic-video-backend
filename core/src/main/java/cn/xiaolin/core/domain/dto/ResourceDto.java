package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对内资源传输对象，字段与 OpenAPI {@code ResourceDto} 及 common-api 副本一致。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    private String md5;
    private String mp4;
    private String m3u8;
    private String mpd;
}
