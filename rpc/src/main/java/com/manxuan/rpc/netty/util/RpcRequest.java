package com.manxuan.rpc.netty.util;

import java.io.Serializable;

  public class RpcRequest implements Serializable {


  private String requestId;
  private String className;
  private String methodName;
  private Class<?>[]parameterTypes;
  private Object[]parameter;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

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

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public void setParameterTypes(Class<?>[] parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  public Object[] getParameter() {
    return parameter;
  }

  public void setParameter(Object[] parameter) {
    this.parameter = parameter;
  }
}
