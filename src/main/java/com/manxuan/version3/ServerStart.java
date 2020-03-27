package com.manxuan.version3;

import com.manxuan.version3.core.BeanContainer;
import com.manxuan.version3.core.Ioc;
import com.manxuan.version3.core.annotation.Autowired;
import com.manxuan.version3.core.annotation.Controller;
import java.io.IOException;
import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class ServerStart {

  @Autowired
  private Server server;

  public void startRun(){
    try{
      server.start();
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    init();
  ((ServerStart)BeanContainer.getInstance().getBean(ServerStart.class)).startRun();
  }

  public static void init() {
    BeanContainer beanContainer = BeanContainer.getInstance();
    beanContainer.loadBean("com.manxuan.version3");
    new Ioc().doIoc();
  }

}
