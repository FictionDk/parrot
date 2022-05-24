package com.fictio.parrot.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Client {

    public static void main(String[] args) {
        int port = 8089;
        if(args.length > 0) port = Integer.parseInt(args[0]);

        TimeClient timeClient = new TimeClient("127.0.0.1", port);
        timeClient.run();
    }

    @AllArgsConstructor
    static class TimeClient {
        private final String host;
        private final int port;
        @SneakyThrows
        public void run(){
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new TimeClientHandler());
                    }
                });

                ChannelFuture f = b.connect(host, port).sync();
                f.channel().closeFuture().sync();
            }finally {
                workerGroup.shutdownGracefully();
            }
        }
    }

    static class TimeClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf m = (ByteBuf) msg;
            try {
                long currentTimeSeconds = (m.readUnsignedInt());
                System.out.println(LocalDateTime.ofEpochSecond(currentTimeSeconds, 0, ZoneOffset.ofHours(8)));
                ctx.close();
            }finally {
                m.release();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
