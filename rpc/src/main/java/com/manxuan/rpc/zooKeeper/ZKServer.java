package com.manxuan.rpc.zooKeeper;

import com.manxuan.rpc.ioc.BeanContainer;
import com.manxuan.rpc.ioc.Ioc;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;


/**
 * 服务器启动，向zk注册一个节点，节点名为ip地址
 */
public class ZKServer{

  /**
   * 注册中心端口号
   */
  private static String connectString = "10.168.1.118:2181";
  private int sessionTimeout = 3000;
  ZooKeeper zkCli = null;
  static String[] interface_name;

  public ZKServer() {
    interface_name = getInterface_name();
    try {
      //连接zkServer
      this.getConnect();
      //将服务注册到注册中心
      this.regist(interface_name);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取该服务端下的所有接口服务
   * @return
   */
  public  String[] getInterface_name() {
    BeanContainer beanContainer = BeanContainer.getInstance();
    beanContainer.loadBean();
    new Ioc().doIoc();
    Set beans = beanContainer.getBeans();
    int length = beans.size();
    String[] name = new String[length];
    if (length == 0) {
      return null;
    } else {
      Iterator it = beans.iterator();
      for (int i = 0; i < length; ) {
        Object bean = it.next();
        Class clz = bean.getClass();
        Class[] inter = clz.getInterfaces();
        for (Class la : inter) {
          String haha = (la.toString().split(" "))[1];
          name[i++] = haha;
        }
      }
      return name;
    }
  }

  /**
   * 定义父节点
   */
  private String parentNode = "/Service/";

  /**
   * 连接zkServer
   */
  public void getConnect() throws IOException {
    zkCli = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      public void process(WatchedEvent event) {
      }
    });
  }

  // 2.注册信息
  public void regist(String[] serviceName) throws KeeperException, InterruptedException {
    String msg;
    for (int i = 0; i < serviceName.length; i++) {
      msg="10.168.1.118:8080-"+serviceName[i];
      zkCli.create(parentNode + serviceName[i], msg.getBytes(), Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL);
    }
  }

  // 3.构造服务器
  public void build() throws InterruptedException {
    System.out.println("10.168.1.118:2181:服务器上线了！");
    Thread.sleep(Long.MAX_VALUE);
  }

}

