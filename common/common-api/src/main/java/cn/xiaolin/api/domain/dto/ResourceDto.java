package cn.xiaolin.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description TODO md5唯一标识，mp4和m3u8，mpd如何填充？md5.mp4 md5.m3u8 md5.mpd，使用boolean标识m3u8和mpd是否存在
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
