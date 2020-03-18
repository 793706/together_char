package com.manxuan.rpc;

import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RpcInvocationHandler implements InvocationHandler {

  private String host;
  private int port;
  private Channel channel;

  public RpcInvocationHandler(String host, int port, Channel channel) {
    this.host = host;
    this.port = port;
    this.channel=channel;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

    //接口类的信息进行封装
    RpcRequest rpcRequest = new RpcRequest();
    RpcResponse rpcResponse=new RpcResponse();

    rpcRequest.setClassName(method.getDeclaringClass().getName());
    rpcRequest.setMethodName(method.getName());
    rpcRequest.setParameter(args);

    //调用远程接口类的实现方法
    channel.writeAndFlush(rpcRequest);

   // RpcResponse

    channel.read();

    return null;
  }
}
