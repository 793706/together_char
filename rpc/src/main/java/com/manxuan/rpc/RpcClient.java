package com.manxuan.rpc;

import com.manxuan.rpc.netty.NettyClient;
import io.netty.channel.Channel;
import java.lang.reflect.Proxy;

public class RpcClient {
  //获取NettyClient对象
  NettyClient client = new NettyClient("127.0.0.1", 8080);
   private Channel channel;

  public RpcClient() throws Exception{
    client.start();
    this.channel = client.getChannel();
  }

@SuppressWarnings("unchecked")
  public <T> T getProxy(Class interfacesClass, String host, int port) {
    return (T)Proxy.newProxyInstance(interfacesClass.getClassLoader(),
        new Class<?>[]{interfacesClass},
        new RpcInvocationHandler(host,port,channel));
  }
}