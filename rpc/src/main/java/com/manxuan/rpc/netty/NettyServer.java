package com.manxuan.rpc.netty;

import com.manxuan.rpc.ServerHandler;
import com.manxuan.rpc.netty.util.JSONSerializer;
import com.manxuan.rpc.netty.util.RpcDecoder;
import com.manxuan.rpc.netty.util.RpcEncoder;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
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

  public void start() throws Exception {
    boss = new NioEventLoopGroup();
    worker = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            //添加请求处理器
            pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
            //添加编码器
            pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
            pipeline.addLast(new ServerHandler());
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
