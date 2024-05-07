package com.kieran.mallchat.common.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public final class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");

    /**
     * TODO:: 为什么要加这个
     */
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    /**
     * 给channel绑定参数
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T data) {
        Attribute<T> attr = channel.attr(key);
        attr.set(data);
    }

    /**
     * 获取channel绑定的参数
     */
    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        return channel.attr(key).get();
    }

}
