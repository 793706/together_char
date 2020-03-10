package com.manxuan.version2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client implements Runnable {

  private static final String HOST = "10.168.1.118";
  private static int PORT = 9999;
  private static SocketChannel socketChannel;
  private static Client client;
  private static String name;
  private static SelectionKey serverKey;
  private static Selector selector;
  private static String ERRORMSG_CONNECTION_REFUSED = "Connection refused: connect";
  static Scanner input = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("请输入你的昵称：随后即可加入聊天室");
    Client.name = input.next();
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
      socketChannel.configureBlocking(false);

      //加入到selector中
      selector = Selector.open();
      serverKey = socketChannel.register(selector, SelectionKey.OP_READ);

      System.out.println("* * 用户" + name + "的客户端 * *");
    } catch (Exception e) {
      if (ERRORMSG_CONNECTION_REFUSED.equals(e.getMessage())) {
        System.out.println("连接不到服务器，请开启服务器");
      } else {
        e.printStackTrace();
        System.out.println("异常错误，请检查您的网络");
      }
      System.exit(1);
    }

    /**
     * 开启线程接受服务端发送的信息
     */
    client = new Client();
    Thread receiverMsg = new Thread(client);
    receiverMsg.start();

    /**
     * 连接后向服务端发送第一条信息，加上标识字符串"open_"和客户端名字
     */
    {
      String msg = "open_" + name;
      try {
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * 进行信息发送
     */
    while (true) {
      String msg = name + "^" + input.next();
      //System.out.println(msg);
      try {
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 接受服务端发送过来的信息
   */
  @Override
  public void run() {
    while (true) {
      String msg = null;
      try {
        int count = selector.select();
        if (count >= 1) {
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            if (key.isReadable()) {
              System.out.println("****************");
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }
}
