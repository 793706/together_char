package com.manxuan.rpc.netty;

import com.manxuan.rpc.interfaces.UserImpl;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.InvocationTargetException;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg)
      throws Exception {
    System.out.println("read from Client"+msg);
    RpcResponse rpcResponse=new RpcResponse();
    rpcResponse.setRequestId(msg.getRequestId());
    try{
      Object handler=handler(msg);
      rpcResponse.setResult(handler);
    }catch (Throwable throwable){
      rpcResponse.setError(throwable.toString());
      throwable.printStackTrace();
    }
    ctx.writeAndFlush(rpcResponse);
  }

  private Object handler(RpcRequest request)throws ClassNotFoundException, InvocationTargetException {
    Class<?>clazz=Class.forName(request.getClassName());
    UserImpl user=new UserImpl();
    Class<?>serviceClass=user.getClass();

    String methodName=request.getMethodName();
    Class<?>[]parametherTypes=request.getParameterTypes();
    Object []parameters=request.getParameter();

    FastClass fastClass=FastClass.create(serviceClass);
    FastMethod fastMethod= fastClass.getMethod(methodName,parametherTypes);

    return fastMethod.invoke(user,parameters);
  }
}
