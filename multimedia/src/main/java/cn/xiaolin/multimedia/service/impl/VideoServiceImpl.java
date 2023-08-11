package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.message.msg.MediaUploadMsg;
import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import cn.xiaolin.multimedia.utils.FileUtil;
import cn.xiaolin.multimedia.service.VideoService;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final FileUtil videoUtil;
    private final IdentifierGenerator multimediaIdGenerator;
    private final ConcurrentMap<Long, MediaUploadMsg> mediaConcurrentMap;

    /**
     * 文件上传
     *
     * @param file MultipartFile格式文件
     * @return 文件id
     */
    @Override
    public Long videoUpload(MultipartFile file) {
        try {
            // 上传文件到服务器
            String filePath = videoUtil.fileUpload(file);
            long resourceId = multimediaIdGenerator.nextId(this).longValue();
            MediaUploadMsg mediaMsg = MediaUploadMsg.builder()
                    .id(resourceId)
                    .name(file.getOriginalFilename())
                    .rawFilePath(filePath)
                    .ossDirPath(videoUtil.getRelativePath())
                    .build();
            mediaConcurrentMap.putIfAbsent(resourceId, mediaMsg);
            return resourceId;
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 视频分片上传
     *
     * @param requestDTO 上传请求
     * @return 上传消息
     */
    @Override
    public FileSliceUploadVo sliceVideoUpload(SliceFileUploadRequestDto requestDTO) {
        try {
            return videoUtil.sliceFileUpload(requestDTO);
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 视频合并
     *
     * @param chunks 视频分片数
     * @param md5    视频摘要md5
     * @return 视频资源ID
     */
    @Override
    public Long sliceVideoMerge(Integer chunks, String md5, String suffix){
        try {
            String filePath = videoUtil.sliceFileMerge(chunks, md5, suffix);

            // 创造一个未使用在的resourceId

            long resourceId = multimediaIdGenerator.nextId(this).longValue();
            MediaUploadMsg mediaMsg = MediaUploadMsg.builder()
                    .id(resourceId)
                    .rawFilePath(filePath)
                    .ossDirPath(videoUtil.getRelativePath())
                    .build();
            mediaConcurrentMap.putIfAbsent(resourceId, mediaMsg);
            return resourceId;
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 视频上传校验
     *
     * @param md5 视频摘要
     * @return 视频资源ID
     */
    @Override
    public Long checkVideo(String md5) {
        String filePath = videoUtil.checkFile(md5);
        if (StringUtils.isEmpty(filePath)) {
            return null;
        } else {
            throw new NotImplementedException();
        }
    }

    /**
     * 暂停后继续上传，返回已经上传的切片序号
     *
     * @param md5 视频摘要md5
     * @return 已经上传的分片序号
     */
    @Override
    public Set<Integer> continueUpload(String md5) {
        return videoUtil.continueUpload(md5);
    }
}
