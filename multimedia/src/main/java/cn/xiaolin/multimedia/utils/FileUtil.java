package cn.xiaolin.multimedia.utils;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/23
 */
public interface FileUtil extends MiniFileStrategy, LargeFileStrategy {

    String getRootDirectory();

    String getRelativePath();
}
