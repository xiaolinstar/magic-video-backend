package cn.xiaolin.multimedia;

import cn.xiaolin.message.service.consumer.MessageConsumer;
import cn.xiaolin.multimedia.config.AppConfigProperties;
import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import cn.xiaolin.multimedia.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 需要启动微服务：Auth Gateway Core Multimedia Has
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频上传测试
 * @create 2025/2/25
 */
@SpringBootTest
@ActiveProfiles("dev")
@EnableConfigurationProperties(AppConfigProperties.class)
public class VideoTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AppConfigProperties appConfigProperties;


    @Test
    public void testAppConfigProperties(){
        System.out.println(appConfigProperties.getVideoFileDir());
        assertNotNull(appConfigProperties.getVideoFileDir());
    }
    /**
     * 视频分片
     */
    @Test
    public void writeVideoChunks() throws IOException {
        String videoFileDir = Path.of(System.getProperty("user.home"), appConfigProperties.getVideoFileDir()).toString();
        String md5 = "magic-video-test";
        String sreFilePath = "/Users/xlxing/IdeaProjects/magic-video-backend/multimedia/Otis & Ruby.mp4";
        byte[] content = Files.readAllBytes(Path.of(sreFilePath));

        Path dirPath = Path.of(videoFileDir, md5);
        File dirFile = dirPath.toFile();
        if (!dirFile.exists() && !dirFile.mkdirs()){
            throw new RuntimeException("创建目录失败" + dirFile);
        }

        int chunkSize = 5 * 1024 * 1024;
        long chunkNum = (long) Math.ceil(1.0*content.length / chunkSize);
        for (int i = 0; i < chunkNum; i++) {
            Path chunkPath = Path.of(videoFileDir, md5, String.valueOf(i));
            File chunkFile = chunkPath.toFile();
            try (FileOutputStream outputStream = new FileOutputStream(chunkFile)){
                // 写文件
                outputStream.write(content, i * chunkSize, Math.min(chunkSize, content.length - i * chunkSize));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String[] names = dirFile.list();
        assert names != null;
        assertEquals(names.length, chunkNum);
    }


    /**
     * 视频分片合并测试
     */
    @Test
    public void videoMergeTest() throws IOException {
        String videoFileDir = Path.of(System.getProperty("user.home"), appConfigProperties.getVideoFileDir()).toString();
        String md5 = "magic-video-test";
        videoService.videoChunksMerge(md5, VideoTypeEnum.MP4);
        Path targetPath = Path.of(videoFileDir, md5+".mp4");
        byte[] bytes = Files.readAllBytes(targetPath);

        // 1. 校验合并后文件大小
        assertEquals(bytes.length, 278717326);

        // 2. 校验视频上传到MinIO，查看 has 实例日志

    }


    /**
     * 视频分片上传测试
     */
    @Test
    public void videoChunkUploadTest() throws IOException {
        String md5 = "md5-xxx-yyy";
        long chunkId = 2L;
        String filePath = "/Users/xlxing/IdeaProjects/magic-video-backend/multimedia/Otis & Ruby.mp4";
        byte[] fileContent = Files.readAllBytes(Path.of(filePath));

        MultipartFile multipartFile = new MockMultipartFile("Otis & Ruby.mp4", fileContent);
        Long nextId = videoService.uploadVideoChunk(multipartFile, md5, chunkId, null);
        assertEquals(3L, nextId);
    }


}
