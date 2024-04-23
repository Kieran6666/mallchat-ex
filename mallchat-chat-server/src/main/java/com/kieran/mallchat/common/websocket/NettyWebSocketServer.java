package com.kieran.mallchat.common.websocket;

import com.kieran.mallchat.common.websocket.handler.HttpRequestHandler;
import com.kieran.mallchat.common.websocket.handler.NettyWebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Configuration 必须加上 否则该类不会被家在
 */
@Slf4j
@Configuration
public class NettyWebSocketServer {
    /**
     * netty服务端单独占用一个端口
     */
    private final static int SERVER_PORT = 8090;

    /**
     * bossGroup处理连接事件，只需要一个线程
     */
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

    /**
     * 如果不设置线程数，默认为以下配置
     * Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
     */
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    private static final NettyWebSocketServerHandler NETTY_WEB_SOCKET_SERVER_HANDLER
            = new NettyWebSocketServerHandler();

    /**
     * 随机器自动启动
     */
    @PostConstruct
    public void run() throws InterruptedException {
        server();
    }

    @PreDestroy
    public void destroy() {
        Future<?> bossFuture = bossGroup.shutdownGracefully();
        Future<?> workerFuture = workerGroup.shutdownGracefully();
        bossFuture.syncUninterruptibly();
        workerFuture.syncUninterruptibly();
    }


    private void server() throws InterruptedException {
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 服务端只能按顺序一个个处理客户端的请求，未能处理的请求放入等待队列
                .option(ChannelOption.SO_BACKLOG, 128)
                // TCP活动探测数据报文
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 给bossGroup加一个日志处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                // handler发生在初始化，childHandler发生在客户端连接后
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        // 心跳检测，每30秒检测一次客户端发送来的心跳请求，客户端每10秒向服务端发送一次心跳请求
                        pipeline.addLast(new IdleStateHandler(30, 0, 0));

                        // WebSocket要通过HTTP协议升级，且要在HTTP中获取用户IP地址
                        pipeline.addLast(new HttpServerCodec());
                        // 以块方式写，添加 chunkedWriter 处理器
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 说明：
                         *  1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
                         *  2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
                         *  TODO:: 为什么要设1024*8
                         */
                        pipeline.addLast(new HttpObjectAggregator(1024 * 8));

                        // 保存用户IP
//                        pipeline.addLast(new HttpRequestHandler());

                        /**
                         * HTTP协议升级WebSocket协议，必须放在Http处理流程之后，因为升级协议后，不会保留用户IP
                         *
                         * 说明：
                         *  1. 对于 WebSocket，它的数据是以帧frame 的形式传递的；
                         *  2. 可以看到 WebSocketFrame 下面有6个子类
                         *  3. 浏览器发送请求时： ws://localhost:7000/hello 表示请求的uri
                         *  4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
                         *      是通过一个状态码 101 来切换的
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));

                        // 自定义的业务处理器
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);

                    }
                }).bind(SERVER_PORT).sync();

    }
}
