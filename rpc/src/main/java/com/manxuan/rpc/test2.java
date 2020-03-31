package com.manxuan.rpc;


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
public class test2{

  /**
   * 注册中心端口号
   */
  private static String connectString = "10.168.1.118:2181";
  private int sessionTimeout = 3000;
  ZooKeeper zkCli = null;
  static String[] interface_name;

  /**
   * 定义父节点
   */
  private String parentNode = "/Service";

  public test2() {
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
   * 利用IOC扫描获取该服务端下的所有接口服务（com.manxuan.rpc.interfaces）
   * @return 返回封装接口名字的数组
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
   * 连接zkServer
   */
  public void getConnect() throws IOException {
    zkCli = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      public void process(WatchedEvent event) {
      }
    });
  }

  /**
   * 创建父节点/Service,此后该服务的所有对外接口信息都注册在该节点下
   * @throws KeeperException
   * @throws InterruptedException
   */
  public void createZnode() throws KeeperException, InterruptedException {
    String path = zkCli.create(parentNode, "world".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    //System.out.println(path);
  }


  // 2.注册信息
  public void regist(String[] serviceName) throws KeeperException, InterruptedException {
    String node=parentNode+"/";
    String msg;
    for (int i = 0; i < serviceName.length; i++) {
      msg="10.168.1.118:8080-"+serviceName[i];
      zkCli.create(node + serviceName[i], msg.getBytes(), Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL);
    }
  }

  // 3.构造服务器
  public void build() throws InterruptedException {
    System.out.println("10.168.1.118:2181:服务器上线了！");
    Thread.sleep(Long.MAX_VALUE);
  }


  public static void main(String[] args) throws InterruptedException {
    test2 test2=new test2();
    Thread.sleep(Long.MAX_VALUE);
  }
}

