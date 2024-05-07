package com.kieran.mallchat.common.wx.service.adapter;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public class WxTextBuilder {

    public static WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMpXmlMessage) {
        return WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                .build();
    }
}
