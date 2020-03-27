package com.manxuan.rpc.interfaces;

import com.manxuan.rpc.ioc.annotation.Bean;
import interfaces.People;


@Bean
public class PeopleImpl implements People {
  @Override
  public String  run(String name) {
    return name+"开始跑步";
  }
}
