package com.kieran.mallchat.common.websocket.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.kieran.mallchat.common.websocket.NettyUtil;
import com.kieran.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.kieran.mallchat.common.websocket.domain.vo.req.WSBaseReq;
import com.kieran.mallchat.common.websocket.service.WebSocketService;
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

    private WebSocketService webSocketService;

    /**
     * 在用户建立连接时，把channel和uid绑定
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffline(ctx);
    }

    /**
     * 第一次建立websocket链接时触发
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("{} - 握手完成", socketAddress);

            // 这里要做token认证，并不需要每次都去请求微信，当token无效时再请求
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("{} - 30秒未请求，超时断连", socketAddress);
                userOffline(ctx);
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    /**
     * 当channel收到内容后触发
     * 这里处理客户端的请求类型
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                // 向微信请求二维码，并返回给客户端
                webSocketService.handleLoginReq(ctx.channel());
                break;
            case HEARTBEAT:
                System.err.println("心跳请求");
                break;
            case AUTHORIZE:
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
                break;
            default:
                log.info("请求类型错误");
        }
    }


    private void userOffline(ChannelHandlerContext ctx) {
        webSocketService.disconnect(ctx.channel());
        ctx.channel().close();
    }

}
