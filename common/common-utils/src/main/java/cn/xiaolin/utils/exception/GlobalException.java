package cn.xiaolin.utils.exception;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 全局异常
 * @create 2023/7/23
 */

public class GlobalException extends RuntimeException{
    public GlobalException(String msg) {
        super(msg);
    }
}
