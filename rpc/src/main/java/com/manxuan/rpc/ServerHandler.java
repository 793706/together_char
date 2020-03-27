package com.manxuan.rpc;


import com.manxuan.rpc.ioc.BeanContainer;
import com.manxuan.rpc.ioc.Ioc;
import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import interfaces.User;
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
    BeanContainer.getInstance().loadBean();
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
   // System.out.println("beans.size="+beans.size());
//
//    //遍历所有bean
//    Iterator it = beans.iterator();
//    while (it.hasNext()) {
//      //匹配
//      Set set = BeanContainer.getInstance()
//          .getClassesBySuper(Class.forName(request.getClassName()));
//      if (set.size() == 0) {
//        System.out.println("未找到实现类");
//      } else if (set.size() == 1) {
//        System.out.println("找到了~~~");
//        Iterator itt = set.iterator();
//        while (itt.hasNext()) {
//          serviceClass =(Class) itt.next();
//          bean=it.next();
//          System.out.println("循环内"+bean.toString());
//        }
//      }
//    }

    Iterator it = beans.iterator();
    while (it.hasNext()) {
      //匹配
      Object ob=it.next();
      Class clz[] = ob.getClass().getInterfaces();
      String s1=clz[0].getName();
      String s2=request.getClassName();
      if (s1.equals(s2)) {
        //System.out.println("找到了~~"+ob.toString());
        serviceClass = (Class) clz[0];
        bean=ob;
      }
    }

    //System.out.println("serviceClass.name"+serviceClass.getName());
    //System.out.println(bean.toString());
    String methodName = request.getMethodName();
    //System.out.println(methodName);
    Class<?>[] parameterTypes = request.getParameterTypes();
    Object[] parameters = request.getParameter();
    FastClass fastClass = FastClass.create(serviceClass);
    FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
    return fastMethod.invoke(bean, parameters);

  }
}