package com.manxuan.rpc.zooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkClient {

  public Map<String,String> server ;
  public static ArrayList<String> serverList;
  private String connectString = "10.168.1.118:2181";
  private int sessionTimeout = 3000;
  ZooKeeper zkCli = null;

  public ZkClient() {
    try{
      // 1.获取连接
      this.getConnect();
      // 2.监听服务的节点信息
      this.getServers();

    }catch (Exception e){
      e.getMessage();
    }
  }

  // 3.业务逻辑
  public void getWatch() throws InterruptedException {
    Thread.sleep(Long.MAX_VALUE);
  }

  /**
   * 获取Service节点下的服务信息
   * 节点名为接口名称，节点信息为（ip地址+":"+端口号+“-”+接口名称）
   * @throws KeeperException
   * @throws InterruptedException
   */
  public void getServers() throws KeeperException, InterruptedException {
    List<String> children = zkCli.getChildren("/Service", true);
    ArrayList<String> serverList = new ArrayList<String>();
    server=new HashMap<>(children.size());

    // 获取每个节点的数据
    for (String c : children) {
      byte[] data = zkCli.getData("/Service/" + c, true, null);

      String[]msg=new String(data).split("-");
//
//      System.out.println("ip地址和端口号"+msg[0]);
//      System.out.println("服务"+msg[1]);

      if (server==null){
        System.out.println(true);
      }

      server.put(msg[1],msg[0]);
      serverList.add(new String(data));
    }
    // 打印服务器列表
    //System.out.println("服务列表"+serverList);

  }



  // 1.连接集群
  public void getConnect() throws IOException {
    zkCli = new ZooKeeper(connectString, sessionTimeout, new Watcher() {

      @Override
      public void process(WatchedEvent event) {
        List<String> children = null;
        try {
          // 监听父节点
          children = zkCli.getChildren("/Service", true);

          // 创建集合存储服务器列表
          ArrayList<String> serverList = new ArrayList<String>();

          // 获取每个节点的数据
          for (String c : children) {
            byte[] data = zkCli.getData("/Service/" + c, true, null);
            serverList.add(new String(data));
          }
          // 打印服务器列表
          //System.out.println(serverList);
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Map<String,String> getMap(){
    return server;
  }
}
