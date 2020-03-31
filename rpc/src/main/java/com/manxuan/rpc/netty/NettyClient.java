package com.manxuan.rpc.netty;

import com.manxuan.rpc.ClientHandler;
import com.manxuan.rpc.netty.util.JSONSerializer;
import com.manxuan.rpc.netty.util.RpcDecoder;
import com.manxuan.rpc.netty.util.RpcEncoder;
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
            ch.pipeline().addLast(new RpcEncoder(RpcRequest.class,new JSONSerializer()));
            ch.pipeline().addLast(new RpcDecoder(RpcResponse.class,new JSONSerializer()));
            pipeline.addLast(clientHandler); //客户端处理类
          }
        });

    ChannelFuture f =bootstrap.connect(host,port).sync();
    if(f.isSuccess()){
      System.out.println("连接服务端成功");
    } else {
      System.out.println("连接失败");
    }
    channel=f.channel();
  }

  public RpcResponse send(final RpcRequest request) {

      channel.writeAndFlush(request);
      while(MsgMap.requestMap.get(request.getRequestId())==null){
      }
    return (RpcResponse) clientHandler.getResponse(request.getRequestId());

  }

}
