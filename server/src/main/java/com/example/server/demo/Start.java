package com.example.server.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

  private static final Logger logger = LoggerFactory.getLogger(Start.class);

  public static void main(String[] args) {
    TCPServer selector = new TCPServer(2001);
    selector.start();
    logger.info("start");
  }

}
