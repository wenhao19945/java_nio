package com.example.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author WenHao
 * @ClassName ClientMain
 * @date 2022/8/1 16:55
 * @Description
 */
public class ClientMain {

  public static void main(String[] args) {
    SocketChannel socketChannel = null;
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8088));
      socketChannel.write(ByteBuffer.wrap("你好世界！".getBytes("utf-8")));
      readBytes(socketChannel);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      if(socketChannel!=null) {
        try {
          socketChannel.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  private static void readBytes(SocketChannel accept) throws IOException {
    int bufferSize = 50;
    ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
    int len = accept.read(byteBuffer);
    while (true) {
      byte[] bytes = new byte[len];
      byteBuffer.flip();
      byteBuffer.get(bytes);
      System.out.println(bytes.length + ":read:" + Arrays.toString(bytes));
      byteBuffer.clear();
      if (len < bufferSize) {
        break;
      }
      len = accept.read(byteBuffer);
    }
  }

}
