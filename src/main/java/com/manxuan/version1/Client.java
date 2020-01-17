package com.manxuan.version1;

import com.manxuan.version1.util.SaveMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client类为客户端类
 */
public class Client implements Runnable {

  //连接错误信息（没有监听此端口的ServerSocket的服务端）
  private static String ERRORMSG_CONNECTION_REFUSED = "Connection refused: connect";
  static Socket socket = null;
  static Scanner input = new Scanner(System.in);
  static String name = "匿名";

  public static void main(String[] args) {
    System.out.println("请输入你的昵称：随后即可加入聊天室");
    Client.name = input.next();

    System.out.println("****一起侃：" + name + "客户端***********");
    try {
      socket = new Socket("192.168.1.58", 9999);
      System.out.println("已经连上服务器，开始聊天。"+"\n"+"群聊：输入信息即可发送到每个群聊用户"+"\n"+"输入{用户昵称}+{:}+{你要发送的信息}即可将{你要发送的信息}发送给{用户昵称}用户");
    } catch (Exception e) {
      if (ERRORMSG_CONNECTION_REFUSED.equals(e.getMessage())) {
        System.out.println("连接不到服务器，请开启服务器");
      } else {
        System.out.println("异常错误，请检查您的网络");
      }
      System.exit(1);
    }
    Client client = new Client();
    Read r = new Read(socket, name);
    Thread print = new Thread(client);
    Thread read = new Thread(r);
    print.start();
    read.start();
  }
  /**
   * 客户端在这里进行信息写入 退出系统时，输入quit即可退出
   */
  @Override
  public void run() {
    try {
      Thread.sleep(1000);
      //获取输入流进行信息写入
      PrintWriter out = new PrintWriter((socket.getOutputStream()));
      while (true) {
        String msg = input.next();
        //用户退出聊天，停止程序
        if ("quit".equals(msg)) {
          out.println(name + ":" + msg);
          out.flush();
          System.exit(1);
        }
        out.println(name + ":" + msg);
        out.flush();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

/**
 * 读取类，从socket获取传过来的数据，将之打印在控制台
 */
class Read implements Runnable {
  private Socket socket = null;
  private String name = "匿名";
  String[] arrmsg = new String[2];
  private String msg = null;
  public Read(Socket socket, String name) {
    this.socket = socket;
    this.name = name;
  }

  @Override
  public void run() {
    SaveMessage saveMessage = new SaveMessage();
    String fromName = "";//信息发送者
    String toName = "";//信息接受者
    String msg = "";//信息
    try {
      Thread.sleep(1000);
      BufferedReader in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
      while (true) {
        msg = in.readLine();
        saveMessage.saveMessageToFile(msg, name);
        //System.out.println(msg);
        //提取出信息来源的消息来源的昵称和发送过来的信息
        //私聊信息格式为[fromname]:[toName]:msg
        //群聊信息格式为[fromname]:msg
        String[] msgFirst = msg.split(":", 2);
        fromName = msgFirst[0];
        msg = msgFirst[1];
        //对发送者发送的信息进行处理，含有指定字符"."为私聊信息
        //私聊信息
        if (msgFirst[1].contains(":")) {
          //删除To字符
          //msgFirst[1] = msgFirst[1].replace(".", "");
          //System.out.println(msgFirst[1]);
          //分割字符，msgSecend[0]为接受信息者名字，msgSecend[1]为发送的信息
          String[] msgSecend = msg.split(":", 2);
          toName = msgSecend[0];
          msg = msgSecend[1];
          pri_println(name, fromName, toName, msg);
        } else {//是群聊信息
          all_PrintAll(name,fromName,msg);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 私聊输出
   *
   * @param localName 执行该方法的昵称
   * @param fromName 发送者昵称
   * @param toName 接受者昵称
   * @param msg 要发送的信息
   */

  private static void pri_println(String localName, String fromName, String toName, String msg) {
    //是否发出消息
    boolean send = true;
    //发送信息的格式
    //{昵称}：
    //          {信息}

    if (localName.equals(toName)) {
      if (localName.equals(fromName)) {
        //自己名字等于接受者并且等于发送者---------自己闹着玩
        //不发送
        send = false;
      } else {
        //自己名字等于接受者不等于发送者-----------自己收到一条私聊消息
        System.out.println("系统提示:收到一条私聊消息");
      }
    } else if (localName.equals(fromName)) {
      //自己名字不等于接受者等于发送者-----------自己发出一条私聊信息
      fromName="你";
      System.out.println("系统提示:向"+toName+"发出一条私聊消息");
    } else {
      //自己名字不等于接受者不等于发送者---------不关你事
      //不发送
      send = false;
    }
    if(send==true) {
      System.out.println(fromName+":");
      System.out.println("  " + msg);
    }
  }

  /**
   *
   * @param localName 执行该方法的客户端的昵称
   * @param fromName 发送该消息的客户端昵称
   * @param msg 要发送的昵称
   */
  public static void all_PrintAll(String localName,String fromName,String msg){
    //发送信息的格式
    //{昵称}：
    //          {信息}
    //你自己发的群聊信息
    if(localName.equals(fromName)){
      fromName="你";
    }
    System.out.println(fromName+":");
    System.out.println("  " + msg);
  }
}