package com.kieran.mallchat.common.wx.service.handler;

import com.kieran.mallchat.common.wx.service.WxMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ScanHandler extends AbstractHandler {

    @Resource
    private WxMsgService wxMsgService;

    /**
     * 用户扫码后触发
     *
     * 这里向用户发送
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager)
            throws WxErrorException {
        this.logger.info("用户扫码 OPENID: {}", wxMpXmlMessage.getFromUser());
        return wxMsgService.scan(wxMpXmlMessage, wxMpService);
    }

}
