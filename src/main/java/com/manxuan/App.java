package com.manxuan;


import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) {
    InetAddress ip;
    try {
      ip=InetAddress.getLocalHost();
      String localnname=ip.getHostName();
      String localip=ip.getHostAddress();
      System.out.println(localip);
    }catch (Exception e){
      e.printStackTrace();
    }

    ///System.out.println("已经连上服务器，开始聊天。"+"\n"+"群聊：输入信息即可发送到每个群聊用户"+"\n"+"输入{用户昵称}+{:}+{你要发送的信息}即可将{你要发送的信息}发送给{用户昵称}用户");
  }
}
