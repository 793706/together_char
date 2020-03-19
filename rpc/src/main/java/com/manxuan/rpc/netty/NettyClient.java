package com.manxuan.rpc.netty;

import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import com.manxuan.rpc.util.MsgMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

  private EventLoopGroup eventLoopGroup;
  private ClientHandler clientHandler;
  private String host;
  private int port;
  private Channel channel;


  //连接服务端的端口号地址和端口号
  public NettyClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void connect() throws Exception {
    eventLoopGroup = new NioEventLoopGroup();
    clientHandler = new ClientHandler();
    Bootstrap bootstrap = new Bootstrap();

    // 使用NioSocketChannel来作为连接用的channel类
    bootstrap.group(eventLoopGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        // 绑定连接初始化器
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            System.out.println("正在连接中...");
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(clientHandler); //客户端处理类
          }
        });

    ChannelFuture f =bootstrap.connect(host,port).sync();
    if(f.isSuccess()){
      System.out.println("连接服务端成功");
    } else {
      System.out.println("重试次数已用完，放弃连接");
    }
    channel=f.channel();
    //发起异步连接请求，绑定连接端口和host信息
//    connect(bootstrap, host, port);
  }


//  private void connect(Bootstrap bootstrap,String host,int port){
//    ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
//      if (future.isSuccess()) {
//        System.out.println("连接服务端成功");
//      } else {
//        System.out.println("重试次数已用完，放弃连接");
//      }
//    });
//    channel = channelFuture.channel();
//  }

  public RpcResponse send(final RpcRequest request) {
    System.out.println("准备发送消息");
      channel.writeAndFlush(request);
      if(MsgMap.requestMap.get(request.getRequestId())==null){
        System.out.println("还没接收到");
      }
    return (RpcResponse) clientHandler.getResponse(request.getRequestId());
  }

}
