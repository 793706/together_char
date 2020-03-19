package com.manxuan.rpc.netty;

public class ServerMain {

  public static void main(String[] args) throws Exception{
    NettyServer nettyServer=new NettyServer();
    nettyServer.start();
  }
}
