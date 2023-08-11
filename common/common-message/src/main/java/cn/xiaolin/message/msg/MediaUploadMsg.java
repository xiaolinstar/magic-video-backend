package cn.xiaolin.message.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件的相关信息：
 * - md5: String 文件的md5值，用来判断将要上传的文件在服务器中是否已经存在
 * - resourceId: Long 资源Id，数据库主键，也是前端请求资源时的凭证
 * - dirPath: 文件夹路径，如/video/02/，以'/'结尾
 * - filePath: 普通文件路径，如/image/01.jpg
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 文件上传到云服务存储的消息
 * @create 2023/7/29
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MediaUploadMsg {
    /** 资源Id */
    private Long id;
    /** 资源名称 */
    private String name;
    /** 摘要算法md5值;判断数据库中是否已经存在，避免重复上传 */
    private String md5;
    /** HLS资源 */
    private String m3u8;
    /** DASH资源 */
    private String mpd;
    /** 切片文件资源目录路径，上传到云存储的路径 */
    @JsonIgnore
    private String ossDirPath;
    /** 文件路径，可以在服务器上寻址  */
    @JsonIgnore
    private String rawFilePath;
}
