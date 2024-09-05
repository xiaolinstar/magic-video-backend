package cn.xiaolin.multimedia.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 文件上传返回消息
 * @create 2023/7/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSliceUploadVo {
    private Integer chunk;
    private Integer chunks;
}
