package com.manxuan.rpc;

import com.manxuan.rpc.netty.NettyClient;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RpcInvocationHandler implements InvocationHandler {

  private NettyClient client;

  public RpcInvocationHandler(NettyClient client) {
    this.client = client;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
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

    RpcResponse rpcResponse=client.send(request);

    Object result=rpcResponse.getResult();
    return rpcResponse.getResult();

  }
}
