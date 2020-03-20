package com.manxuan.rpc.netty;

import com.manxuan.rpc.ioc.BeanContainer;
import com.manxuan.rpc.ioc.Ioc;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
  public BeanContainer beanContainer;

  public ServerHandler() {
    this.beanContainer =BeanContainer.getInstance();
    beanContainer.loadBean();
    new Ioc().doIoc();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg)
      throws Exception {
    System.out.println("read from Client "+msg);
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

  @Override
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    super.channelWritabilityChanged(ctx);
  }

  private Object handler(RpcRequest request){
    try {
      Class<?>clazz=Class.forName(request.getClassName());
      System.out.println();
      Object serverBean=BeanContainer.getInstance().getBean(clazz);

      System.out.println("serverBean= "+serverBean.toString());

      Class<?> serviceClass = serverBean.getClass();
      String methodName = request.getMethodName();
      //System.out.println("methodName= " + methodName);

      Class<?>[] parameterTypes = request.getParameterTypes();
      Object[] parameters = request.getParameter();

      FastClass fastClass = FastClass.create(serviceClass);
      FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);

      return fastMethod.invoke(serverBean, parameters);
    }catch (Exception e){
      e.printStackTrace();
      return e.getCause();
    }
  }
}
