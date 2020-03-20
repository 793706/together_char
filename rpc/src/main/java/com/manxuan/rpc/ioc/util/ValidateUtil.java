package com.manxuan.rpc.ioc.util;

public class ValidateUtil {

public static boolean isEmpty(Object obj){
  return obj==null;
}
  public static boolean isEmpty(String obj){
    return (obj==null||"".equals(obj));
  }

  public static boolean isNotEmpty(Object obj){
    return !isEmpty(obj);
  }
  public static boolean isNotEmpty(String obj){
    return !isEmpty(obj);
  }
}
