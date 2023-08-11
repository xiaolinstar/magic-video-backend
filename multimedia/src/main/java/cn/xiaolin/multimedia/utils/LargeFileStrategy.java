package cn.xiaolin.multimedia.utils;


import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Set;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 大文件上传策略
 * @create 2023/7/8
 */
public interface LargeFileStrategy {
    /**
     * 分片上传文件
     * @param requestDto 文件上传请求信息
     * @return 文件上传信息回复
     */
    FileSliceUploadVo sliceFileUpload(SliceFileUploadRequestDto requestDto) throws IOException;

    /**
     * 合并文件，并进行md5校验
     * @param chunks 文件总分片数
     * @param md5 源文件的md5值
     * @param suffix 文件拓展符号
     * @return 文件唯一标识符，资源ID
     */
    String sliceFileMerge(Integer chunks, String md5, String suffix) throws IOException;

    /**
     * 校验服务器是否已经存在将要传输的文件
     * 服务器端已经存在用户将要传输的文件，服务器直接返回目标文件
     * @param md5 文件的md5值
     * @return 可寻址的文件路径
     */
    String checkFile(String md5);


    /**
     * 断点续传
     * @param md5 文件的md5值
     * @return 已经传输的分片的序号
     */
    Set<Integer> continueUpload(String md5);
}
