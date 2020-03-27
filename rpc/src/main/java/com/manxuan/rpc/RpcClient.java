package com.manxuan.rpc;

import com.manxuan.rpc.zooKeeper.ZkClient;
import java.lang.reflect.Proxy;
import java.util.Map;


public class RpcClient {

public Map<String,String>server;

  public RpcClient(){
    //向注册中心获取服务列表
    ZkClient zkClient=new ZkClient();
    this.server=zkClient.getMap();
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(Class interfacesClass) {
    String ipHost=getIpHost(interfacesClass);
    return (T) Proxy.newProxyInstance(interfacesClass.getClassLoader(),
        new Class<?>[]{interfacesClass},
        new RpcInvocationHandler(ipHost));
  }

  public String getIpHost(Class clz){
    String ipHost=null;
    //获取指定服务的ip地址和端口号
    for(String key:server.keySet()){
      if(key.equals(clz.toString().split(" ")[1])){
        ipHost=server.get(key);
      }
    }
    if(ipHost!=null){
      return ipHost;
    }else {
      return null;
    }

  }
}