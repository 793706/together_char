package com.manxuan.version3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {

  public void start(String nickName) throws IOException {
    // 连接服务器
    SocketChannel socketChannel = SocketChannel.open(
        new InetSocketAddress("10.168.1.118", 9999)
    );

    // 接收服务器端响应
    // 创建线程，专门负责接收服务器端的响应数据
    // selector socketChannel 注册
    Selector selector = Selector.open();
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ);
    new Thread(new ClientHandler(selector,nickName)).start();

    // 第一次连接，向服务器端发送客户端个人信息（名字）
    Scanner scanner = new Scanner(System.in);
    {
        socketChannel.write(Charset.forName("UTF-8").encode("UserName:" + nickName));
    }

    // 向服务器端发送数据

    while (scanner.hasNextLine()) {
      String request = scanner.nextLine();
      if ((request != null) && (request.length() > 0)) {
        socketChannel.write(Charset.forName("UTF-8").encode(nickName + ":" + request));
      }
    }
  }

}
