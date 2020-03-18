package com.manxuan.rpc.netty.util;

public class RpcResponse {

  private String className;
  private String methodName;
  private Object[]parameter;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Object[] getParameter() {
    return parameter;
  }

  public void setParameter(Object[] parameter) {
    this.parameter = parameter;
  }
}
