package com.kieran.mallchat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.kieran.mallchat.common.common.domain.dto.RequestInfo;
import com.kieran.mallchat.common.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestInfo requestInfo = new RequestInfo();
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.ATTRIBUTE_UID)).map(Object::toString)
                .map(Long::parseLong).orElse(null);

        // 这里处理HTTP请求时，用户的IP地址，用于IP归属地查询，websocket和http都需要获取IP
        String ip = ServletUtil.getClientIP(request);

        requestInfo.setUid(uid);
        requestInfo.setIp(ip);
        RequestHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
