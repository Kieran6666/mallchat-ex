package com.kieran.mallchat.common.wx.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.service.UserService;
import com.kieran.mallchat.common.user.service.adapter.UserAdapter;
import com.kieran.mallchat.common.websocket.service.WebSocketService;
import com.kieran.mallchat.common.wx.service.WxMsgService;
import com.kieran.mallchat.common.wx.service.adapter.WxTextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WxMsgServiceImpl implements WxMsgService {

    private static final String AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?"
            + "appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


    private static final ConcurrentHashMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();


    @Value("${wx.mp.callback}")
    private String callBack;

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    @Resource
    private WebSocketService webSocketService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage, WxMpService wxMpService) {
        String eventKey = wxMpXmlMessage.getEventKey();
        if (null == eventKey) {
            return null;
        }

        // code用来找出channel的数据
        Integer loginCode = Integer.valueOf(eventKey.replace("qrscene_", ""));

        // 获取用户微信的OPEN_ID
        String openId = wxMpXmlMessage.getFromUser();

        User user = userService.getByOpenId(openId);
        boolean ifRegister = Objects.nonNull(user);
        boolean ifAuthorized = ifRegister && StringUtils.isNotEmpty(user.getAvatar());

        // 如果用户已注册且已授予权限，则直接验证登陆token
        if (ifRegister && ifAuthorized) {
            webSocketService.scanLoginSuccess(loginCode, user.getId());
        }

        if (!ifRegister) {
            User insert = User.builder().openId(openId).build();
            userService.register(insert);
        }

        // 请求用户授权，接下来去callback接口处理后续结果
        if (!ifAuthorized) {
            // 保存openId与code，后续通过code获取channel
            WAIT_AUTHORIZE_MAP.put(openId, loginCode);
            // 通知客户端用户扫码成功，等待授权
            webSocketService.scanSuccess(loginCode);

            // 用户授权后的回调通知接口，后续流程都在该接口去继续跟踪
            String url = String.format(AUTH_URL, wxMpService.getWxMpConfigStorage().getAppId(),
                    URLEncoder.encode(callBack + "/wx/portal/public/callback"));
            String content = "请点击授权链接：<a href=\"" + url + "\">登录</a>";
            // 这里必须自己封装一个，默认sdk中没有fromUser toUser
            return WxTextBuilder.build(content, wxMpXmlMessage);
        }

        return WxTextBuilder.build("欢迎来访", wxMpXmlMessage);
    }

    /**
     * 用户授权成功，保存用户信息
     */
    @Override
    public void authorized(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());
        if (StrUtil.isBlank((user.getAvatar()))) {
            fillUserInfo(userInfo, user.getId());
        }
        Integer loginCode = WAIT_AUTHORIZE_MAP.remove(userInfo.getOpenid());
        webSocketService.scanLoginSuccess(loginCode, user.getId());
    }

    private void fillUserInfo(WxOAuth2UserInfo userInfo, Long uid) {
        User updater = UserAdapter.buildAuthorizedUser(uid, userInfo);

        for (int i = 0; i < 5; i++) {
            try {
                userDao.updateById(updater);
                return;
            } catch (DuplicateKeyException e) {
                log.info("fill userInfo duplicate uid:{},info:{}", uid, userInfo);
            } catch (Exception e) {
                log.error("fill userInfo fail uid:{},info:{}", uid, userInfo);
            }
            updater.setName("名字重置" + RandomUtil.randomInt(100000));
        }
    }
}
