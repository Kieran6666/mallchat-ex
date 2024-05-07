package com.kieran.mallchat.common.websocket.service.adapter;

import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
import com.kieran.mallchat.common.websocket.domain.vo.resp.WSBaseResp;
import com.kieran.mallchat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.kieran.mallchat.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * 通过多态的形式返回VO
 *
 * 适配器模式
 */
public class WebSocketAdapter {

    /**
     * 返回登陆链接
     */
    public static WSBaseResp<?> buildLoginUrlResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    /**
     * 扫码成功，等待授权
     */
    public static WSBaseResp<?> buildScanSuccessResp() {
        WSBaseResp<?> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    /**
     * 登陆成功
     */
    public static WSBaseResp<WSLoginSuccess> buildLoginSuccessResp(User user, String token, boolean hasPower) {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(hasPower ? 1 : 0)
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    /**
     * 无效的TOKEN
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<?> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALID_TOKEN.getType());
        return resp;
    }
}
