package com.example.client.demo;

public class StartClient {

  public static void main(String args[]) throws Exception{
    String [] param = new String[]{"127.0.0.1","2001","test"};
    TCPClient.start(param);
  }

}
