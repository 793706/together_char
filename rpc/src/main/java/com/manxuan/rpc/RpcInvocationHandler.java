package com.manxuan.rpc;

import com.manxuan.rpc.netty.NettyClient;
import com.manxuan.rpc.zooKeeper.ZkClient;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

public class RpcInvocationHandler implements InvocationHandler {

  private NettyClient client;
  public  ZkClient zkClient;

  public Map<String,String> server;

  public RpcInvocationHandler(String ipAndHost) {
    this.zkClient=new ZkClient();
    this.server=zkClient.getMap();

    //获取服务端的ip地址和端口号
    String []ipHost=ipAndHost.split(":");
    System.out.println(ipHost[0]);
    System.out.println(new Integer(ipHost[1]));
    client = new NettyClient("10.168.1.118",8080);
    try {
      client.connect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    //接口类的信息进行封装
    RpcRequest request = new RpcRequest();
    String requestId = UUID.randomUUID().toString();

    String className=method.getDeclaringClass().getName();
    String methodName=method.getName();
    Class<?>[]parameterTypes=method.getParameterTypes();

    request.setRequestId(requestId);
    request.setClassName(className);
    request.setMethodName(methodName);
    request.setParameterTypes(parameterTypes);
    request.setParameter(args);


    System.out.println(request.toString());
    RpcResponse rpcResponse=client.send(request);

    //Object result=rpcResponse.getResult();
    return rpcResponse.getResult();

  }
}
