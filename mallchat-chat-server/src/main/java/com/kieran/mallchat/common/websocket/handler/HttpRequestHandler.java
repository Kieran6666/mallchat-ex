package com.kieran.mallchat.common.websocket.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.kieran.mallchat.common.websocket.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * 客户端请求是独立的，这里不存在线程安全问题，不需要设置为@sharable
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

        // 获取token参数
        String token = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("token"))
                .map(CharSequence::toString).orElse("");
        NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);

        // 获取请求路径
        request.setUri(urlBuilder.getPath().toString());
        String ip = request.headers().get("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            ip = inetSocketAddress.getAddress().getHostAddress();
        }
        NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);

        // HTTP连接请求只需要处理一次，用完即删
        ctx.pipeline().remove(this);

        // HTTP请求处理完成后，把请求传递给下一个处理器
        ctx.fireChannelRead(request);
    }
}
