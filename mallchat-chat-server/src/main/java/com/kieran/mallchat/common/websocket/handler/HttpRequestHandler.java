package com.kieran.mallchat.common.websocket.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.kieran.mallchat.common.websocket.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * 客户端请求是独立的，这里不存在线程安全问题，不需要设置为@sharable
 *
 * 这里不能使用SimpleChannelInboundHandler<FullHttpRequest>, 因为会这个实现类会自动释放资源，无法继续向后传递
 */
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 获取token参数，
            // 这一步是把websocket连接和token认证，合二为一了
            String token = Optional.ofNullable(urlBuilder.getQuery()).map(k->k.get("token")).map(CharSequence::toString).orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);

            // 获取请求路径，再次设置，因为websocket协议是/结尾，没有?后续的参数
            request.setUri(urlBuilder.getPath().toString());
            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {//如果没经过nginx，就直接获取远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        }else
        {
            ctx.fireChannelRead(msg);
        }
    }
}
