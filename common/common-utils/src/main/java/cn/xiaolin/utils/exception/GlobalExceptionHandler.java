package cn.xiaolin.utils.exception;

import cn.xiaolin.utils.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 统一异常处理
 * @create 2023/7/23
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public Result<String> globalExceptionHandler(GlobalException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> unknownExceptionHandler(Exception e) {
        log.error(e.getMessage());
        return Result.serverError();
    }
}
