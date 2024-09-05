package cn.xiaolin.multimedia.aop;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.msg.MediaUploadMsg;
import cn.xiaolin.message.msg.MediaResourcePublisher;
import cn.xiaolin.multimedia.service.AliOssService;
import cn.xiaolin.multimedia.service.MediaKitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 文件上传AOP增强
 * @create 2023/7/29
 */

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class FileInterceptor {
    private final AliOssService aliOssService;
    private final ConcurrentMap<Long, MediaUploadMsg> mediaConcurrentHashMap;
    private final MediaKitService mediaKitService;
    private final MediaResourcePublisher mediaResourcePublisher;
    private final ConcurrentMap<String, Long> resourceIdMap;

    /**
     * 视频文件上传到服务器切点
     * 视频上传完成后，转码为HLS或DASH格式
     */
    @Pointcut("execution(public * cn.xiaolin.multimedia.service.VideoService.videoUpload(..))" +
            " || execution(public * cn.xiaolin.multimedia.service.VideoService.sliceVideoMerge(..))")
    public void media2HlsPointCut() {
    }


    /**
     * 使用AOP方式在视频转码切点
     * 转码后的视频上传到阿里云OSS等云存储中
     */
    @Pointcut("execution(public * cn.xiaolin.multimedia.service.MediaKitService.media2Hls(..))")
    public void uploadAliyunPointCut(){
    }

    @AfterReturning(pointcut = "media2HlsPointCut()", returning = "result")
    public void afterReturningMediaSaved(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 1 && result instanceof Long resourceId) {

            MediaUploadMsg mediaUploadMsg = mediaConcurrentHashMap.get(resourceId);
            String rawFilePath = mediaUploadMsg.getRawFilePath();
            File rawFile = new File(rawFilePath);
            // 异步任务
            mediaKitService.media2Hls(rawFile);
            resourceIdMap.putIfAbsent(rawFilePath, resourceId);
        } else {
            log.error("切点参数不正确，AOP失效");
        }
    }

    /**
     * 将切片文件上传到云服务器
     * @param joinPoint 切点参数
     */
    @After("uploadAliyunPointCut()")
    public void doAfterMediaConvert(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof File file) {

            // 获取文件路径
            String rawFilePath = file.getPath();
            // 填充m3u8属性
            Long resourceId = resourceIdMap.get(rawFilePath);
            MediaUploadMsg mediaUploadMsg = mediaConcurrentHashMap.get(resourceId);
            // 文件名当作云服务器端的目录名
            String nameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf("."));
            String ossDirectoryPath = Paths.get(mediaUploadMsg.getOssDirPath(), nameWithoutExtension).toString();
            mediaUploadMsg.setM3u8(Paths.get(ossDirectoryPath, "1080p.m3u8").toString());

            String targetDirPath = Paths.get(file.getParent(), nameWithoutExtension).toString();
            File targetDir = new File(targetDirPath);
            aliOssService.uploadDirectory2Oss(targetDir, ossDirectoryPath);

            // 发送RabbitMQ消息
            mediaResourcePublisher.mediaUploadSuccess(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE, mediaUploadMsg);
        } else {
            log.error("AOP失败：" + joinPoint.getSignature().getName());
        }
    }

}
