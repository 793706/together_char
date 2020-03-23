package com.manxuan.rpc.netty;


import com.manxuan.rpc.ioc.BeanContainer;
import com.manxuan.rpc.ioc.Ioc;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

  public BeanContainer beanContainer;

  public ServerHandler() {
    beanContainer = BeanContainer.getInstance();
    beanContainer.loadBean();
    Set set = beanContainer.getBeans();
    new Ioc().doIoc();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
    RpcResponse rpcResponse = new RpcResponse();
    rpcResponse.setRequestId(msg.getRequestId());
    try {
      Object handler = handler(msg);
      rpcResponse.setResult(handler);
    } catch (Throwable throwable) {
      rpcResponse.setError(throwable.toString());
      throwable.printStackTrace();
    }
    ctx.writeAndFlush(rpcResponse);
  }


  private Object handler(RpcRequest request)
      throws ClassNotFoundException, InvocationTargetException {
    Class<?> serviceClass=null;
    Object bean=null;

    //获取所有Bean实例
    Set beans = BeanContainer.getInstance().getBeans();

    //遍历所有bean
    Iterator it = beans.iterator();
    while (it.hasNext()) {
      bean = it.next();

      //匹配
      Set set = BeanContainer.getInstance()
          .getClassesBySuper(Class.forName(request.getClassName()));
      if (set.size() == 0) {
        System.out.println("未找到实现类");
      } else if (set.size() == 1) {
        Iterator itt = set.iterator();
        while (itt.hasNext()) {
          serviceClass =(Class) itt.next();
        }
      }
    }
    String methodName = request.getMethodName();
    Class<?>[] parameterTypes = request.getParameterTypes();
    Object[] parameters = request.getParameter();
    FastClass fastClass = FastClass.create(serviceClass);
    FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
    return fastMethod.invoke(bean, parameters);

  }
}