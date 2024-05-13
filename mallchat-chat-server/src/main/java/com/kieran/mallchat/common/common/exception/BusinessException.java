package com.kieran.mallchat.common.common.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

    private Integer errorCode;
    private String errorMsg;

    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorCode = ExceptionErrorNum.BUSINESS_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(ExceptionErrorNum exceptionErrorNum) {
        super(exceptionErrorNum.getErrorMsg());
        this.errorCode = exceptionErrorNum.getErrorCode();
        this.errorMsg = exceptionErrorNum.getErrorMsg();
    }
}
