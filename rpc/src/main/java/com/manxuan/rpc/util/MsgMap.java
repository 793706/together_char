package com.manxuan.rpc.util;

import java.util.HashMap;
import java.util.Map;

public class MsgMap {

  private static MsgMap msgMap = new MsgMap();

  /**
   * requuestMap 需要请求的数据，string为关键字，object为发送的数据
   *
   * callBackMap 接受到的数据，String为关键字，object为发送的数据
   */
  public static Map<String, Object> requestMap = new HashMap<>();
  public static Map<String, Object> callBackMap = new HashMap<>();

  public static void addRequest(String key, Object obj) {
    requestMap.put(key, obj);
  }

  public static void removeRequest(String key) {
    requestMap.remove(key);
  }

  public static void getRequesMap(String key) {
    requestMap.get(key);
  }

  public static void addCallBackMap(String key, Object obj) {
    callBackMap.put(key, obj);
  }

  public static void getCallBackMap(String key) {
    callBackMap.remove(key);
  }

  public static void removeCallBackMap(String key) {
    callBackMap.get(key);
  }
}
