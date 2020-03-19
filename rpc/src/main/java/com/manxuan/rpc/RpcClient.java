package com.manxuan.rpc;

import com.manxuan.rpc.netty.NettyClient;

import java.lang.reflect.Proxy;

public class RpcClient {
private String host;
private int port;
NettyClient client;

  public RpcClient(String host, int port) throws Exception{
    this.host = host;
    this.port = port;
    client = new NettyClient(host,port);
    client.connect();
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(Class interfacesClass) {
    return (T) Proxy.newProxyInstance(interfacesClass.getClassLoader(),
        new Class<?>[]{interfacesClass},
        new RpcInvocationHandler(client));
  }
}