package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.service.MediaKitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 视频处理套件
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaKitServiceImpl implements MediaKitService {
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    @Override
    @Async("mediaExecutor")
    public void media2Hls(File file) {
        String nameWithoutExt = file.getName().substring(0, file.getName().lastIndexOf("."));
        String parentPath = file.getParent();
        File targetDir = new File(parentPath + '/' + nameWithoutExt);
        if (!targetDir.exists()) {
            boolean succeed = targetDir.mkdirs();
            if (!succeed) {
                throw new GlobalException("创建文件夹'" + targetDir + "'失败");
            }
        }

        String audioCodec = "aac";
        String videoCodec = "libx264";
        String outputPath1080p = targetDir + "/1080p.m3u8";
        String outputPath720p = targetDir + "/720p.m3u8";
        String outputPath360p = targetDir + "/360p.m3u8";
        String hlsTime = "-hls_time";
        String hlsListSize = "-hls_list_size";
        String hlsSegmentFilename = "-hls_segment_filename";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(file.getPath())
                .addOutput(outputPath1080p)
                .setFormat("hls")
                .setAudioCodec(audioCodec)
                .setVideoCodec(videoCodec)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .addExtraArgs("-vf", "scale=1920:1080")
                .addExtraArgs(hlsTime, "10")
                .addExtraArgs(hlsListSize, "0")
                .addExtraArgs(hlsSegmentFilename, targetDir + "/output_1080p_%03d.ts")
                .done()
                .addOutput(outputPath720p)
                .setFormat("hls")
                .setAudioCodec(audioCodec)
                .setVideoCodec(videoCodec)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .addExtraArgs("-vf", "scale=1280:720")
                .addExtraArgs(hlsTime, "10")
                .addExtraArgs(hlsListSize, "0")
                .addExtraArgs(hlsSegmentFilename, targetDir + "/output_720p_%03d.ts")
                .done()
                .addOutput(outputPath360p)
                .setFormat("hls")
                .setAudioCodec(audioCodec)
                .setVideoCodec(videoCodec)
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .addExtraArgs("-vf", "scale=640:360")
                .addExtraArgs(hlsTime, "10")
                .addExtraArgs(hlsListSize, "0")
                .addExtraArgs(hlsSegmentFilename, targetDir + "/output_360p_%03d.ts")
                .done();

        try {
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            FFmpegProbeResult in = ffprobe.probe(file.getPath());
            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
                final double durationNs = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
                @Override
                public void progress(Progress progress) {
                    int percentage = (durationNs > 0) ? (int) (progress.out_time_ns / durationNs * 100) : 99;
                    // 日志中输出转换进度信息
                    log.debug("[{}%] status: {}, frame: {}, time: {} ms, fps: {}, speed: {}x",
                            percentage,
                            progress.status,
                            progress.frame,
                            FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                            progress.fps.doubleValue(),
                            progress.speed
                    );
                }
            });
            job.run();

        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }

    }
}
