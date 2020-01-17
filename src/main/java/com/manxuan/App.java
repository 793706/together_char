package com.manxuan;


import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) {
    String msg="001:002:你好呀";
    String[] msgFirst = msg.split(":", 2);
    System.out.println(msgFirst[0]);
    System.out.println(msgFirst[1]);
    String[]msgSecend= msgFirst[1].split(":",2);
    System.out.println(msgSecend[0]);
    System.out.println(msgSecend[1]);
//    while (true) {
//      Scanner input = new Scanner(System.in);
//      String msg = input.next();
//      //提取出信息来源的客户端昵称和客户端输入的信息
//      String[] msgFirst = msg.split(":", 2);
//      if (-1 != msgFirst[1].indexOf("To")) {
//        System.out.println("是私聊信息");
//        msgFirst[1] = msgFirst[1].replace("To", "");
//        String[] msgSecend = msgFirst[1].split(":", 2);
//        String toName = msgSecend[0];
//        msg = msgSecend[1];
//        msgFirst[1].split(":");
//        System.out.println("发送者是" + msgFirst[0]);
//        System.out.println("接受者是" + toName);
//        System.out.println("要发送的信息是" + msg);
//      } else {
//        System.out.println("发送者是" + msgFirst[0]);
//        System.out.println("要发送的信息是" + msgFirst[1]);
//      }
//    }
  }
}
