package cn.xiaolin.multimedia.utils;

import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.domain.vo.FileSliceUploadVo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description File Kit
 * @create 2023/7/23
 */
@NoArgsConstructor
public class FileUtilImpl implements FileUtil {
    /**
     * 文件的根目录
     */
    private String rootDirectory;

    /**
     * 存放文件的文件夹
     */
    private String fileDirectory;

    public FileUtilImpl(String rootDirectory, String fileDirectory) throws FileSystemException {
        this.rootDirectory = rootDirectory;
        File rootFile = new File(this.rootDirectory);
        if (!rootFile.exists()) {
            boolean created = rootFile.mkdirs();
            if (!created) {
                throw new FileSystemException("文件夹创建失败 " + this.rootDirectory);
            }
        } else if (!rootFile.isDirectory()) {
            throw new FileSystemException("根目录不合法，已存在非同名文件");
        }

        this.fileDirectory = fileDirectory;
        File file = new File(getWorkDirectory());
        if (file.exists() && file.isFile()) {
            throw new FileSystemException(" 文件夹创建失败，已经存在同名非文件夹文件");
        }
        if (!file.exists()) {
            boolean res = file.mkdirs();
            if (!res) {
                throw new FileSystemException("创建文件夹失败");
            }
        }
    }

    public FileUtilImpl(String fileDirectory) throws FileSystemException {
        // 默认的当前工作目录
        this(System.getProperty("user.dir"), fileDirectory);
    }


    /**
     * 分片上传文件
     *
     * @param requestDto 文件上传请求信息
     * @return 文件上传信息回复
     */
    @Override
    public FileSliceUploadVo sliceFileUpload(SliceFileUploadRequestDto requestDto) throws IOException {
        String md5 = requestDto.getMd5();
        Integer chunk = requestDto.getChunk();
        MultipartFile file = requestDto.getFile();
        // 使用md5作为临时分片目录

        File fileDir = new File(Paths.get(getWorkDirectory(), md5).toString());
        if (!fileDir.exists()) {
            boolean res = fileDir.mkdirs();
            if (!res) {
                throw new FileSystemException("创建文件夹失败 " + Paths.get(getWorkDirectory(), md5));
            }
        }
        String targetFilename = Paths.get(getWorkDirectory(), md5, String.valueOf(chunk)).toString();
        byte[] bytes = file.getBytes();

        // 写文件
        Path targetPath = Path.of(targetFilename);
        Files.write(targetPath, bytes);
        return FileSliceUploadVo.builder()
                .chunk(chunk)
                .chunks(requestDto.getChunks())
                .build();
    }

    /**
     * 合并文件，并进行md5校验
     *
     * @param chunks 文件总分片数
     * @param md5    源文件的md5值
     * @param suffix 文件拓展符号
     * @return 可寻址的文件路径
     */
    @Override
    public String sliceFileMerge(Integer chunks, String md5, String suffix) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path destPath = Paths.get(getWorkDirectory(), uuid + '.' + suffix);

        // 1. 检查文件分块是否已经完整
        List<Integer> notExistChunks = new ArrayList<>();
        for (int i=0; i<chunks; i++) {
            Path srcPath = Paths.get(getWorkDirectory(), md5, String.valueOf(i));
            if (!srcPath.toFile().exists()) {
                notExistChunks.add(i);
            }
        }
        if (!CollectionUtils.isEmpty(notExistChunks)) {
            String collected = notExistChunks.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            throw new GlobalException("文件片段不完整，无法进行合并，缺少：" + collected);
        }

        // 2. 文件合并
        for (int i=0; i<chunks; i++) {
            Path srcPath = Paths.get(getWorkDirectory(), md5, String.valueOf(i));
            Files.write(destPath, Files.readAllBytes(srcPath), StandardOpenOption.APPEND);
        }

        // 3. 校验md5
        try (FileInputStream fileInputStream = new FileInputStream(destPath.toFile())) {
            String targetFileMd5 = DigestUtils.md5DigestAsHex(fileInputStream);

            boolean success = Objects.equals(md5, targetFileMd5);
            if (!success) {
                throw new GlobalException("目标文件与源文件的MD5不一致");
            }
            return destPath.toFile().getPath();
        } catch (FileNotFoundException e) {
            throw new GlobalException("文件合并时发生错误");
        }
    }

    /**
     * 校验服务器是否已经存在将要传输的文件
     * 服务器端已经存在用户将要传输的文件，服务器直接返回目标文件
     *
     * @param md5 文件的md5值
     * @return 可寻址的文件路径
     */
    @Override
    public String checkFile(String md5) {
        return null;
    }

    /**
     * 断点续传
     *
     * @param md5 文件的md5值
     * @return 已经传输的分片的序号
     */
    @Override
    public Set<Integer> continueUpload(String md5) {
        Path pathDir = Paths.get(getWorkDirectory(), md5);
        if (!Files.exists(pathDir)) {
            throw new GlobalException("客户端错误，文件不存在");
        }
        Optional<String[]> chunkName = Optional.ofNullable(pathDir.toFile().list());
        return Arrays.stream(chunkName.orElse(new String[]{}))
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * 文件上传
     *
     * @param file 图像
     * @return 可寻址的文件路径
     */
    @Override
    public String fileUpload(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String suffix = getMultipartFileExtension(file);
        String targetFilePath = Paths.get(getWorkDirectory(), uuid + '.' + suffix).toString();
        File newFile = new File(targetFilePath);
        file.transferTo(newFile.getAbsoluteFile());
        return newFile.getPath();
    }

    /**
     * 字符串加密上传文件
     *
     * @param fileBase64 图像的64位加密
     * @return 可寻址的文件路径
     */
    @Override
    public String fileUpload(String fileBase64) {
        return null;
    }
    /**
     * ServletOutputStream流式加载
     * 返回值必须为void
     *
     * @param response   HttpServletResponse
     * @param filePath 文件路径
     */
    @Override
    public void fileLoad(HttpServletResponse response, String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                response.setContentType("image/jpg");
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = fileInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                    outputStream.flush();
                }
            }
        }
    }


    /**
     * 文件base64编码
     *
     * @param filePath 可寻址的文件路径
     * @return base64字符串文件
     */
    @Override
    public String fileLoad(String filePath) {
        return null;
    }

    /**
     * 获得工作目录
     * @return 工作目录的地址
     */
    private String getWorkDirectory() {
        return Paths.get(this.rootDirectory, this.fileDirectory).toString();
    }

    private String getMultipartFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1);
            }
        }
        throw new GlobalException("未知错误");
    }

    @Override
    public String getRootDirectory() {
        return this.rootDirectory;
    }

    @Override
    public String getRelativePath() {
        return this.fileDirectory;
    }
}
