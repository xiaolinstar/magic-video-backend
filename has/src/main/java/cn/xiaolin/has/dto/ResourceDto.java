package cn.xiaolin.has.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thin transport copy of the core internal {@code ResourceDto}.
 * HAS must not depend on the core module; fields mirror {@code contracts/openapi/core-internal.v1.yaml}.
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
