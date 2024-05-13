package com.kieran.mallchat.common.common.exception;

import com.kieran.mallchat.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常配置
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder msg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(z -> msg.append("[").append(z.getField()).append("]")
                .append(z.getDefaultMessage()).append(","));
        String errorMsg = msg.substring(0, msg.length() - 1);
        log.error("validation error:{}", errorMsg);
        return ApiResult.fail(ExceptionErrorNum.PARAM_VALID.getErrorCode(), errorMsg);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException, {}", e.getErrorMsg());
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 最后一道防线
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> throwableHandler(Throwable t) {
        log.error(t.getMessage());
        return ApiResult.fail(ExceptionErrorNum.SYSTEM_ERROR);
    }


}
