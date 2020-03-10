package com.manxuan.version2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class Server implements Runnable {

  private Selector selector;
  private SelectionKey serverKey;
  private Vector<String> usernames;
  private static final int PORT = 9999;

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public Server() {
    usernames = new Vector<String>();
    init();
  }

  public void init() {
    try {
      selector = Selector.open();
      //创建serverSocketChannel
      ServerSocketChannel serverChannel = ServerSocketChannel.open();
      ServerSocket socket = serverChannel.socket();
      socket.bind(new InetSocketAddress(PORT));

      //加入到selector中
      serverChannel.configureBlocking(false);
      serverKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
      printInfo("server starting.......");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        //获取就绪channel
        int count = selector.select();
        if (count > 0) {
          Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
          while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            //若此key的通道是等待接收新的套接字连接
            if (key.isAcceptable()) {
              System.out.println(key.toString() + ":接收");
              //再将这个accept状态的服务器key去掉
              iterator.remove();
              ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
              //接收socket
              SocketChannel socket = serverChannel.accept();
              socket.configureBlocking(false);
              //将channel加入到selector中，并一开始读取数据
              socket.register(selector, SelectionKey.OP_READ);
            }
            //若此key通道是有数据可读状态
            if (key.isValid() && key.isReadable()) {
              readMsg(key);
            }
            //若此key通道是写数据状态
            if (key.isValid() && key.isWritable()) {
              writeMsg(key);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public void readMsg(SelectionKey key) {
    SocketChannel channel = null;
    try {
      channel = (SocketChannel) key.channel();
      //设置buffer缓冲区
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      //假如客户端关闭了通道，这里在对该通道read数据，会发生IOException，捕获到Exception后，关闭掉该channel，取消该key
      int count = channel.read(buffer);
      StringBuffer buf = new StringBuffer();
      //如果读取到数据
      if (count > 0) {
        //让buffer翻转，把buffer中的数据读取出来
        buffer.flip();
        buf.append(new String(buffer.array(), 0, count));
      }
      String msg = buf.toString();

      System.out.println("客户端发来"+msg);

      //如果此数据是客户端连接时发送的数据
      if (msg.indexOf("open_") != -1) {
        //取出名字
        String name = msg.substring(5);
        printInfo(name + " --> online");
        usernames.add(name);
        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

        while (iter.hasNext()) {
          SelectionKey skey = iter.next();
          //若不是服务器套接字通道的key，则将数据设置的到key中
          //并更新此key感兴趣的动作
          if (skey != serverKey) {
            skey.attach(usernames);
            skey.interestOps(skey.interestOps() | SelectionKey.OP_WRITE);
          }
        }

        //如果是下线时发送的数据
      } else if (msg.indexOf("exit") != -1) {
        String username = msg.substring((5));
        usernames.remove(username);
        key.attach("close");

        //要退出当前channel加上close的标识，并把标志转为写，如果write中收到了close，则中断channel的连接
        key.interestOps(SelectionKey.OP_WRITE);
        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
        while ((iter.hasNext())) {
          SelectionKey skey = iter.next();
          skey.attach((usernames));
          skey.interestOps(skey.interestOps() | SelectionKey.OP_WRITE);
        }

        //如果是聊天发送的数据
      } else {
        //System.out.println(msg);
        String uname = msg.substring(0, msg.indexOf(("^")));
        msg = msg.substring(msg.indexOf("^") + 1);
        printInfo("(" + uname + ")说：" + msg);
        String dateTime = sdf.format(new Date());
        String smsg = uname + " " + dateTime + "\n" + msg + "\n";
        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
        while (iter.hasNext()) {
          SelectionKey skey = iter.next();

          skey.attach(smsg);
          skey.interestOps(skey.interestOps() | SelectionKey.OP_WRITE);
        }
      }
      buffer.clear();
    } catch (IOException e) {
      key.cancel();
      try {
        channel.socket().close();
        channel.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

  }

  public void writeMsg(SelectionKey key) {
  try{
    SocketChannel channel =(SocketChannel)key.channel();
    Object attachment =key.attachment();

    //获取key的值之后，要把key的值置空，避免影响下一次的使用
    key.attach("");

    channel.write(ByteBuffer.wrap(attachment.toString().getBytes()));
    key.interestOps(SelectionKey.OP_READ);
  }catch (Exception e){
    e.printStackTrace();
    }
  }

  private void printInfo(String str) {
    System.out.println("[" + sdf.format(new Date()) + "] -> " + str);
  }

  public static void main(String[] args) {
    Server server = new Server();
    new Thread(server).start();
  }

}
