package com.kieran.mallchat.common.websocket.service;


import io.netty.channel.Channel;

public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void disconnect(Channel channel);

    void scanSuccess(Integer loginCode);

    Boolean scanLoginSuccess(Integer loginCode, Long uid);

    void authorize(Channel channel, String token);

}
