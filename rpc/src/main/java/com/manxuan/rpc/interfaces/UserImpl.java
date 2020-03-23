package com.manxuan.rpc.interfaces;

import com.manxuan.rpc.ioc.annotation.Bean;
import interfaces.User;

@Bean
public class UserImpl implements User {

  @Override
  public String addUser(int id) {
    return "id为"+id+"的用户添加成功";
  }
}
