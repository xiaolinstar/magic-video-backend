package cn.xiaolin.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/11
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
