package com.kieran.mallchat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Charsets;
import com.kieran.mallchat.common.common.domain.vo.resp.ApiResult;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTP 未登陆返回
 */
@AllArgsConstructor
public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登陆失效，请重新登陆");

    private Integer errorCode;
    private String errorMsg;


    public void sendHttpErrorResp(HttpServletResponse response) throws IOException {
        response.setStatus(this.getErrorCode());
        ApiResult resp = ApiResult.fail(this);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JSONUtil.toJsonStr(resp));
    }

    @Override
    public Integer getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
