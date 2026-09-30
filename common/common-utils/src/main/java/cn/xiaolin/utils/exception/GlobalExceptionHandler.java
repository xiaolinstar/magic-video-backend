package cn.xiaolin.utils.exception;

import cn.xiaolin.utils.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

/**
 * Global exception handler.
 *
 * Stack traces and internal error messages are only exposed in non-production
 * profiles. In production we log the details server-side and return a generic
 * message + correlation id so support engineers can still trace the issue.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/7/23
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @ExceptionHandler(GlobalException.class)
    public Result<String> globalExceptionHandler(GlobalException e) {
        // Business exceptions: safe to surface their message to the caller.
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> unknownExceptionHandler(Exception e) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Unhandled exception [correlationId={}]", correlationId, e);
        if (isProduction()) {
            // Don't leak stack traces / SQL / filesystem paths to clients in prod.
            return Result.error("服务器内部错误，请稍后重试 (" + correlationId + ")");
        }
        return Result.error("[" + correlationId + "] " + e.getMessage());
    }

    private boolean isProduction() {
        if (activeProfile == null) {
            return false;
        }
        String p = activeProfile.trim().toLowerCase();
        return p.equals("prod") || p.equals("production");
    }
}
