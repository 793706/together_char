package com.manxuan.rpc.interfaces;

import com.manxuan.rpc.ioc.annotation.Bean;
import java.io.Serializable;

@Bean
public class test implements Serializable {

  public void say(){
    System.out.println("ajajjajaj");
  }

}
