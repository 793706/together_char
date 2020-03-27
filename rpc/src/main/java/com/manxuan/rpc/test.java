package com.manxuan.rpc;

import com.manxuan.rpc.ioc.BeanContainer;
import com.manxuan.rpc.ioc.Ioc;
import com.manxuan.rpc.netty.util.RpcRequest;
import java.util.Iterator;
import java.util.Set;

public class test {

  public static void main(String[] args) throws ClassNotFoundException {
    BeanContainer.getInstance().loadBean();
    new Ioc().doIoc();

    Class<?> serviceClass = null;
    Object bean = null;
    RpcRequest request = new RpcRequest();
    request.setClassName("interfaces.User");

    //获取所有Bean实例
    Set beans = BeanContainer.getInstance().getBeans();
    System.out.println("beans.size=" + beans.size());

    //遍历所有bean
    Iterator it = beans.iterator();
    while (it.hasNext()) {
      Object ob=it.next();
      Class clz[] = ob.getClass().getInterfaces();
      if (clz[0].getName() == request.getClassName()) {
        serviceClass = (Class) clz[0];
        bean=ob;
      }
    }

    System.out.println(serviceClass.getName());
    System.out.println(bean.toString());

  }
}

