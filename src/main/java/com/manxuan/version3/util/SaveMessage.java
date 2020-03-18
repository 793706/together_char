package com.manxuan.version3.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class SaveMessage {

  /**
   * 获取当前项目目录
   * @return 当前目录
   */
  private String getUrl() {
    String fileDirectory = "";
    try {
      File directory = new File("./src/main/file2");
      fileDirectory = directory.getCanonicalPath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileDirectory;
  }

  /**
   * @param filename 获取指定文件名的文件目录
   */
  private String getFile(String filename) {
    File file=null;
    try {
      file = new File(getUrl() + "\\" + filename + "的聊天记录.txt");
      if (file.exists()) {
        return file.toString();
      } else {
        Writer os = new FileWriter(file);
        os.write(filename + "的聊天记录");
        os.flush();
        os.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file.toString();
  }

  /**
   * @param msg 要写进文件的信息
   * @param name 要进行操作的文件名
   */
  public void saveMessageToFile(String msg, String name) {
    try{
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getFile(name),true));
      bufferedWriter.append("\n");
      bufferedWriter.append(msg);
      bufferedWriter.flush();
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
