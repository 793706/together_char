package com.manxuan.version1;

import com.manxuan.version1.util.SaveMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Server为服务器类
 */
public class Server implements Runnable {

  //存放socket，用于向每一个客户端发送信息
  static List<Socket> socketList = new ArrayList<>();
  static Socket socket = null;
  static ServerSocket serverSocket = null;
  static int count = 0;

  public Server() {
    try {
      serverSocket = new ServerSocket(9999);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 调用该方法可以先所有客户端发送消息
   *
   * @param msg 需要发送的信息
   */
  public  void printToAll(String msg) {
    try {
      for (int i = 0; i < socketList.size(); i++) {
        Socket socket = socketList.get(i);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(msg);
        out.flush();
      }
    } catch (IOException e) {
    }
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    System.out.println("**********一起侃服务端**********");
    Server server = new Server();

    //一直循环，随时接受新的客户端连接
    while (true) {
      try {
        //accept为阻塞方法，会一直等待客户端连接
        socket = serverSocket.accept();
        count++;
        System.out.println("系统：第" + count + "个客户已连接");
        System.out.println("系统公告：当前聊天室在线人数：" + count);
        //将客户端存放进入list集合
        socketList.add(socket);
      } catch (IOException e) {
        e.printStackTrace();
      }
      //线程read接受客户端信息并且广播到各个客户端
      Thread read = new Thread(server);
      read.start();
    }
  }

  /**
   * 获取发送到服务端的数据，并且发送到socketlist里面的各个客户端（广播）
   */
  @Override
  public void run() {
    SaveMessage saveMessage=new SaveMessage();
    String name = null;
    String[] msg = new String[2];
    try {
      Thread.sleep(1000);
      //读取从传输到服务端的数据，getInputStream，获取读入流
      BufferedReader in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
      //一直循环
      while (true) {
        String import_info = in.readLine();
        msg = import_info.split(":", 2);
        name = msg[0];
        if("quit".equals(msg[1])){
          System.out.println(name+"退出系统");
          printToAll("系统:"+name+"用户退出聊天，当前聊天室在线人数：" + (--count));
          saveMessage.saveMessageToFile(name+"用户退出系统","Server");
        }else {
          saveMessage.saveMessageToFile(import_info,"Server");
          System.out.println(import_info);
          //向每一个socket发送信息
          printToAll(import_info);
        }
      }
    } catch (Exception e) {
    }
  }
}
