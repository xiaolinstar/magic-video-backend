package cn.xiaolin.utils.exception;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 全局异常
 * @create 2023/7/23
 */

public class GlobalException extends RuntimeException{
    public GlobalException(String msg) {
        super(msg);
    }
}
