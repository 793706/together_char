package com.manxuan;


import com.manxuan.version3.util.PropertiesUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) throws Exception {
    System.out.println(PropertiesUtil.getValue("hahah"));
    Test test = new Test();
    Socket client=new Socket();
    ObjectInputStream input= new ObjectInputStream(client.getInputStream());
    input.readUTF();
  }
}

class Test implements Runnable {

  @Override
  public void run() {

  }
}
