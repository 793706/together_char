package com.manxuan.rpc.netty;

public class ServerMain {

  public static void main(String[] args) throws Exception{
    new NettyServer().bind(8080);
  }
}
