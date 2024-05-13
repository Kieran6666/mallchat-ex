package com.kieran.mallchat.common.common.interceptor;

import com.kieran.mallchat.common.common.exception.HttpErrorEnum;
import com.kieran.mallchat.common.user.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";

    @Resource
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            request.setAttribute(ATTRIBUTE_UID, validUid);
        } else {
            // 未登陆，判断当前接口是否需要登陆
            String[] split = request.getRequestURI().split("/");
            boolean ifPublic = split.length > 3 && "public".equals(split[3]);
            if (!ifPublic) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpErrorResp(response);
                return false;
            }
        }
        return true;
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(authorization)
                .filter(z -> z.startsWith(AUTHORIZATION_SCHEMA))
                .map(z -> z.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
