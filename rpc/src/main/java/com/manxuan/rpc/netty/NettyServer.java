package com.manxuan.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

  private EventLoopGroup boss = null;
  private EventLoopGroup worker = null;
  private ServerHandler serverHandler;

  public void start() throws Exception {
    boss = new NioEventLoopGroup();
    worker = new NioEventLoopGroup();

    serverHandler=new ServerHandler();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            //添加请求处理器
            pipeline.addLast(serverHandler);
          }
        });
    bind(serverBootstrap,8080);
  }

  public void bind(ServerBootstrap serverBootstrap, int port) throws Exception {
    ChannelFuture future = serverBootstrap.bind(port).sync();
    if (future.isSuccess()) {
      System.out.println("服务端启动成功");
    } else {
      System.out.println("服务端启动失败");
      future.cause().printStackTrace();
      boss.shutdownGracefully(); //关闭线程组
      worker.shutdownGracefully();
    }
  }
}
