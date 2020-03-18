package com.manxuan.rpc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class test {

  public static void main(String[] args) {
    Map<String, Object> requestMap = new Hashtable<>();

    System.out.println(requestMap.size());
    requestMap.put("001",1);
    System.out.println(requestMap.size());

  }
}
