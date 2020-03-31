package com.manxuan.rpc;

import com.manxuan.rpc.netty.NettyServer;
import com.manxuan.rpc.zooKeeper.ZKServer;

public class ServerMain {

  public static void main(String[] args) throws Exception{

    //向注册中心注册服务
    ZKServer zkServer =new ZKServer();

    //开启Netty，随时接收服务请求
    NettyServer nettyServer=new NettyServer();

    nettyServer.start();

    zkServer.build();
  }
}
