package com.manxuan.rpc;

import com.manxuan.rpc.zooKeeper.ZkClient;
import com.manxuan.rpc.zooKeeper.ZooKeeperProSync;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;


public class RpcClient {

public Map<String,String>server;
public ZooKeeperProSync zooKeeperProSync;

  public RpcClient(){
//    //向注册中心获取服务列表
//    ZkClient zkClient=new ZkClient();
//    this.server=zkClient.getMap();

    zooKeeperProSync=new ZooKeeperProSync();
    zooKeeperProSync.getConnect();
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(Class interfacesClass) {
    //获取服务的ip地址和端口号
    //if()
    String ipHost=getIpHost(interfacesClass);
    return (T) Proxy.newProxyInstance(interfacesClass.getClassLoader(),
        new Class<?>[]{interfacesClass},
        new RpcInvocationHandler(ipHost));
  }

  /**
   * 根据参数获取对应服务的ip地址和端口号
   * @param clz
   * @return
   */
  public String getIpHost(Class clz){
    String ipHost=null;
    //获取指定服务的ip地址和端口号
    server=zooKeeperProSync.getService();
    if(server==null){
      return null;
    }
    Set <String>set=zooKeeperProSync.getService().keySet();
    //System.out.println("set.size="+set.size());
    for(String key:set){
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