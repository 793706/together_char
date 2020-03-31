package com.manxuan.rpc.zooKeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * 分布式配置中心demo
 * @author
 */
public class ZooKeeperProSync {

  public Map<String,String> server=new HashMap<String, String>(10);
  private static ZooKeeper zk;
  private static final String CONNECT_STRING = "10.168.1.118:2181";
  private static final int SESSION_TIMEOUT = 50000;
  private static final String PARENT = "/Service";

  public void getConnect(){
    try{
    zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {

      @SneakyThrows
      @Override
      public void process(WatchedEvent event) {
        saveService();
        String path = event.getPath();
        EventType type = event.getType();
        KeeperState state = event.getState();
        //System.out.println(path + "\t" + type + "\t" + state);
        saveService();
        // 循环监听
          zk.getChildren(PARENT, true);
      }
    });
      zk.getChildren(PARENT, true);
      //Thread.sleep(Long.MAX_VALUE);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 获取Service下的服务,将服务保存到服务列表中
   *
   */
  public  void saveService(){
    try {
      List<String> children = zk.getChildren("/Service", true);
      ArrayList<String> serverList = new ArrayList<String>();
//      server=new HashMap<>(children.size());

      // 获取每个节点的数据
      for (String c : children) {
        //System.out.println(c);
        byte[] data = zk.getData("/Service/" + c, true, null);
        String[] msg = new String(data).split("-");
        server.put(msg[1], msg[0]);
        serverList.add(new String(data));
      }

      System.out.println("服务列表"+serverList);
    }catch (Exception e){
      e.printStackTrace();
    }
    // 打印服务器列表

  }

  public Map getService(){
    return server;
  }
}