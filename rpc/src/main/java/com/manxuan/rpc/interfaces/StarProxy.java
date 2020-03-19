package com.manxuan.rpc.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class StarProxy implements InvocationHandler {

  Object object;

  public void setTarget(Object object) {
    this.object = object;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result = method.invoke(object, args);
    return result;
  }

  public Object CreatProxyedObj() {
    return Proxy
        .newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
            this);
  }

}
