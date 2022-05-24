package com.fictio.parrot.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;

public class Server {
    @SneakyThrows
    public static void main(String[] args) {
        int port = 8089;
        if(args.length > 0) port = Integer.parseInt(args[0]);

        NettyServer server = new NettyServer(port, new TimeServerHandler());
        server.run();
    }

    static class NettyServer {
        private final int port;
        private final ChannelHandler handler;
        public NettyServer(int port, ChannelHandler handler){
            this.port = port;
            this.handler = handler;
        }
        public void run() throws InterruptedException {
            // NioEventLoopGroup是一个处理I/O操作的多线程事件循环
            EventLoopGroup bossGroup = new NioEventLoopGroup(); // 接受传入连接
            EventLoopGroup workGroup = new NioEventLoopGroup(); // 实际工作线程
            try{
                ServerBootstrap b = new ServerBootstrap(); // 设置服务器的辅助类
                // 指定使用NioServerSocketChannel类,用于实例化新的Channel来接受传入的连接(服务端通道类型)
                b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override  // 随着程序复杂,可以将这个匿名类提取到顶级类中
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(handler);
                            }
                        }).option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            }finally {
                workGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    /**
     * 接收消息并丢弃
     * 1. 继承ChannelInboundHandlerAdapter
     * 2. 实现channelRead方法
     * 3. 实现exceptionCaught方法
     */
    @SuppressWarnings("unused")
    static class DiscardServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // super.channelRead(ctx, msg);
            // ByteBuf is a reference-counted object which has to be released explicitly via the release() method
            // ((ByteBuf) msg).release();  忽略请求需要通过release方法显示的释放,可直接release,但通常按下面的方式处理
            ByteBuf in = (ByteBuf) msg;
            try{
                while (in.isReadable()){
                    System.out.printf("c=%c,",in.readByte());
                    System.out.flush();
                }
                // System.out.println(in.toString(CharsetUtil.US_ASCII)); 等同于上面的写法
            }finally {
                ReferenceCountUtil.release(msg);
            }

        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }
    }

    /**
     * 授时服务
     * 不接受任何请求,返回时间戳
     * 发送消息后立即关闭连接
     */
    static class TimeServerHandler extends ChannelInboundHandlerAdapter {
        // 重写channelActive,不需要读取数据--channelRead
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            final ByteBuf time = ctx.alloc().buffer(4);
            int timestamp = (int) (System.currentTimeMillis() / 1000L);
            System.out.println("T="+timestamp);
            time.writeInt(timestamp);

            final ChannelFuture f = ctx.writeAndFlush(time);
            f.addListener((ChannelFutureListener) future -> {
                assert f == future;
                ctx.close();
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
