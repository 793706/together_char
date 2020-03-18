package com.manxuan.version3.util;

import com.manxuan.version3.core.annotation.Service;
import java.io.FileInputStream;
import java.util.Properties;

@Service
public class PropertiesUtil {
  private static Properties properties;

  public static String getValue(String key){
    System.out.println("getvalue------------");
    try{
      properties=new Properties();
      properties.load(new FileInputStream("src/main/resources/server.properties"));
    }catch (Exception e){
      System.out.println("未找到配置文件");
      e.printStackTrace();
    }
    return properties.getProperty(key);
  }

}
