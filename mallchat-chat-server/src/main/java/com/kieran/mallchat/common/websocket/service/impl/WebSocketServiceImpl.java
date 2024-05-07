package com.kieran.mallchat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.service.LoginService;
import com.kieran.mallchat.common.user.service.UserService;
import com.kieran.mallchat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.kieran.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.kieran.mallchat.common.websocket.service.WebSocketService;
import com.kieran.mallchat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 管理所有用户的连接（登陆态/游客）
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 登陆请求最大请求数
     */
    private static final long MAX_SIZE = 1000;

    /**
     * 二维码过期时间
     */
    private static final Duration EXPIRE_TIME = Duration.ofHours(1);

    /**
     * 登陆二维码
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP
            = Caffeine.newBuilder().maximumSize(MAX_SIZE).expireAfterWrite(EXPIRE_TIME).build();

    @Resource
    private UserService userService;

    /**
     * 微信SDK
     */
    @Lazy
    @Resource
    private WxMpService wxMpService;

    @Resource
    private LoginService loginService;

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    // TODO:: SneakyThrows 为什么
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        Integer code = generateLoginCode(channel);
        WxMpQrCodeTicket wxMpQrCodeTicket
                = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        sendMsg(channel, WebSocketAdapter.buildLoginUrlResp(wxMpQrCodeTicket));
    }

    @Override
    public void disconnect(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    /**
     * 生成随机数
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull((WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel))));
        return code;
    }

    /**
     * 这里可以使用《JWT 双TOKEN的解决办法》，必须研究一下
     * @param loginCode
     * @param uid
     */
    @Override
    public Boolean scanLoginSuccess(Integer loginCode, Long uid) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.isNull(channel)) {
            return Boolean.FALSE;
        }

        User user = userService.getByUid(uid);

        WAIT_LOGIN_MAP.invalidate(loginCode);

        // 调用用户登陆模块
        String token = loginService.login(uid);

        // TODO:: power
        boolean power = false;

        sendMsg(channel, WebSocketAdapter.buildLoginSuccessResp(user, token, power));
        return Boolean.TRUE;
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (Objects.nonNull(uid)) {
            User user = userService.getByUid(uid);
            sendMsg(channel, WebSocketAdapter.buildLoginSuccessResp(user, token, false));
        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);

        if (Objects.nonNull(channel)) {
            sendMsg(channel, WebSocketAdapter.buildScanSuccessResp());
        }
    }
}
