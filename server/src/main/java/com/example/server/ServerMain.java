package com.example.server;

import com.example.server.thread.BasicThreadFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WenHao
 * @ClassName ServerMain
 * @date 2022/8/1 16:05
 * @Description
 */
public class ServerMain {

  public static void main(String[] args) {
    System.out.println("ServerMain");
    int threadCount = 4;
    ServerSocketChannel serverSocketChannel = null;
    try {
      serverSocketChannel = ServerSocketChannel.open();
      //绑定监听端口
      serverSocketChannel.bind(new InetSocketAddress(8088));
      //设置为非阻塞模式
      serverSocketChannel.configureBlocking(false);
      System.out.println("server wait connect....");
      ScheduledExecutorService newFixedThreadPool = new ScheduledThreadPoolExecutor(threadCount, commonThreadFactory());
      //ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadCount);
      final ServerSocketChannel channel = serverSocketChannel;
      // 使用线程方式处理连接
      AtomicInteger count = new AtomicInteger(0);

      //
      for (int i = 0; i < threadCount; i++) {
        NioChannelHandler handler = new NioChannelHandler();
        newFixedThreadPool.submit(new NioServerChannelAccepter(channel, handler, count));
      }

      // 记录当前客户端数量
      while (true) {
        int i = count.get();
        System.out.println("当前客户端数量：" + i);
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      if (serverSocketChannel != null) {
        System.out.println("server shutdown now....");
        try {
          serverSocketChannel.close();
        } catch (IOException e2) {
          // TODO Auto-generated catch block
          e2.printStackTrace();
        }
      }
    }
  }

  public static ThreadFactory commonThreadFactory() {
    return new BasicThreadFactory.Builder()
        .namingPattern("example-schedule-pool-%d")
        .uncaughtExceptionHandler((t, e) ->  System.out.println("failed to run task on thread {}."+ t + e))
        .daemon(true)
        .build();
  }


  private static class NioServerChannelAccepter implements Runnable {
    private final ServerSocketChannel channel;
    private final AtomicInteger clientCount;
    private final NioChannelHandler handler;

    public NioServerChannelAccepter(ServerSocketChannel channel, NioChannelHandler handler,
        AtomicInteger clientCount) {
      this.channel = channel;
      this.clientCount = clientCount;
      this.handler = handler;
    }

    @Override
    public void run() {
      SocketChannel accept = null;
      while (true) {
        try {
          if (accept == null) {
            try {
              System.out.println(Thread.currentThread().getName() + ":wait to connect");
              Thread.sleep(2000L);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            accept = channel.accept();
            if (accept != null) {
              clientCount.incrementAndGet();
            }
          } else {
            handler.handler(accept);
          }
        } catch (Exception e) {
          e.printStackTrace();
          if (accept != null) {
            clientCount.decrementAndGet();
            try {
              accept.close();
            } catch (IOException e2) {
              // TODO Auto-generated catch block
              e2.printStackTrace();
            }
            accept = null;
          }

        }
      }

    }

  }

  private static class NioChannelHandler {
    public void handler(SocketChannel accept) throws IOException {
      SocketAddress remoteAddress = accept.getRemoteAddress();
      System.out.println("server received a connected :" + remoteAddress);
      // 开始读取数据
      readBytes(accept);
      byte[] bytes = "你好客户端".getBytes();
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      // ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
      // byteBuffer.put(bytes);
      int write = accept.write(byteBuffer);
      System.out.println("write bytes to client:" + write);
      byteBuffer.clear();
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
