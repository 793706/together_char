package com.manxuan.rpc.interfaces;

public class UserImpl implements User {

  public static void main(String[] args) {
    System.out.println("");
  }

  @Override
  public String addUser(int id) {
    return id+"success";
  }
}
