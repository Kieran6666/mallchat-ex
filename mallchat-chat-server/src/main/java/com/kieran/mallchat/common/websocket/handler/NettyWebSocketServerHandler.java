package com.kieran.mallchat.common.websocket.handler;

import cn.hutool.json.JSONUtil;
import com.kieran.mallchat.common.websocket.NettyUtil;
import com.kieran.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.kieran.mallchat.common.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;


@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                log.info("{} - 30秒未请求，超时断连", socketAddress);
                ctx.channel().close();
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("{} - 握手完成", socketAddress);
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);

        }

        super.userEventTriggered(ctx, evt);
    }

    /**
     * 这里处理客户端的请求类型
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                System.err.println("登陆请求");
                ctx.channel().writeAndFlush(new TextWebSocketFrame("请求成功"));
                break;
            case HEARTBEAT:
                System.err.println("心跳请求");
                break;
            case AUTHORIZE:
                System.err.println("认证请求");
                break;
            default:
                log.info("请求类型错误");
        }

    }
}
