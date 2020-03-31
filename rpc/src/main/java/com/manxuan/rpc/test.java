package com.manxuan.rpc;

import com.manxuan.rpc.zooKeeper.ZooKeeperProSync;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


/**
 * 服务器启动，向zk注册一个节点，节点名为ip地址
 */
public class test {

  public static Map<String,String> server ;
  static Scanner scanner = new Scanner(System.in);
  static List<String> children = null;
  public static ZooKeeperProSync zooKeeperProSync;

  public static void main(String[] args) throws Exception {
    zooKeeperProSync = new ZooKeeperProSync();
    zooKeeperProSync.getConnect();

    while (true) {
      System.out.println("请输入：");
      String request = scanner.nextLine();
      if(request.equals("1")){
        pirct();
      }
    }
  }

  public static void pirct() {
    server=zooKeeperProSync.getService();
    Set<String> set=server.keySet();
    for(String key:set){
      System.out.println(server.get(key));
    }
  }
}


