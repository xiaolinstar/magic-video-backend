package cn.xiaolin.multimedia.domain.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 分片文件上传请求
 * @create 2023/7/23
 */
@Tag(name = "分片文件上传数据传输对象")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SliceFileUploadRequestDto {
    private String md5;
    private MultipartFile chunkVideo;
    private String chunkMd5;
    private Long chunkId;
}
