package com.manxuan.version3;

import com.manxuan.version3.core.annotation.Autowired;
import com.manxuan.version3.core.annotation.Service;
import com.manxuan.version3.util.PropertiesUtil;
import com.manxuan.version3.util.SaveMessage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


@Service
public class Server {

  public static HashMap<String, SocketChannel> nameList = new HashMap<>();
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SaveMessage saveMessage = new SaveMessage();

  //@Autowired
  //public PropertiesUtil propertiesUtil;


  public void start() throws IOException {

    // 1. 创建Selector
    Selector selector = Selector.open();

    // 2. 通过ServerSocketChannel创建channel通道
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

    // 3. 为channel通道绑定监听端口

    serverSocketChannel.bind(new InetSocketAddress(Integer.parseInt(PropertiesUtil.getValue("server_Port"))));

    // 4. 设置channel为非阻塞模式
    serverSocketChannel.configureBlocking(false);

    // 5. 将channel注册到selector上，监听连接事件
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("服务器启动成功！");

    // 6. 循环等待新接入的连接
    for (; ; ) {
      // 获取可用channel的数量
      int readyChannels = selector.select();
      if (readyChannels == 0) {
        continue;
      }
      // 获取可用channel的集合
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator iterator = selectionKeys.iterator();
      while (iterator.hasNext()) {
        // selectionKey实例
        SelectionKey selectionKey = (SelectionKey) iterator.next();
        // 移除Set中的当前selectionKey
        iterator.remove();

        // 7. 根据就绪状态，调用对应方法处理业务逻辑
        // 如果是 接入事件
        if (selectionKey.isAcceptable()) {
          acceptHandler(serverSocketChannel, selector);
        }
        // 如果是 可读事件
        if (selectionKey.isReadable()) {
          readHandler(selectionKey, selector);
        }
      }
    }
  }

  /**
   * 接入事件处理器
   */
  private void acceptHandler(ServerSocketChannel serverSocketChannel,
      Selector selector) throws IOException {
    // 如果要是接入事件，创建socketChannel
    SocketChannel socketChannel = serverSocketChannel.accept();
    // 将socketChannel设置为非阻塞工作模式
    socketChannel.configureBlocking(false);
    // 将channel注册到selector上，监听 可读事件
    socketChannel.register(selector, SelectionKey.OP_READ);

    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    // 循环读取客户端请求信息
    String firstMsg = "";
    while (socketChannel.read(byteBuffer) > 0) {
      // 切换buffer为读模式
      byteBuffer.flip();
      // 读取buffer中的内容
      firstMsg += Charset.forName("UTF-8").decode(byteBuffer);
    }

    socketChannel.write(Charset.forName("UTF-8")
        .encode("你与聊天室里其他人都不是朋友关系，请注意隐私安全"));

  }

  /**
   * 可读事件处理器
   */
  private void readHandler(SelectionKey selectionKey, Selector selector)
      throws IOException {
    // 要从selectionKey中获取到已经就绪的channel
    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
    // 创建buffer
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    // 循环读取客户端请求信息
    String request = "";
    while (socketChannel.read(byteBuffer) > 0) {
      // 切换buffer为读模式
      byteBuffer.flip();
      // 读取buffer中的内容
      request += Charset.forName("UTF-8").decode(byteBuffer);
    }
    // 将channel再次注册到selector上，监听他的可读事件
    socketChannel.register(selector, selectionKey.OP_READ);

    String savemsg = "[" + sdf.format(new Date()) + "]" + request;
    //保存聊天记录到本地
    saveMessage.saveMessageToFile(savemsg, "Server");

    //是客户端下线通知,带关键字quit
    if (request.indexOf("quit") != -1) {
      String uname = request.substring(0, request.indexOf((":")));
      broadCast(selector, socketChannel, "用户" + uname + "下线");

      //保存聊天记录到本地
      saveMessage.saveMessageToFile("用户" + uname + "下线", "Server");
      //关闭通道
      SocketChannel socketChannel1 = nameList.get(uname);
      socketChannel1.close();
      nameList.remove(uname);

      //是客户端第一条信息，带个人信息
    } else if (request.indexOf("UserName:") != -1) {
      String uname = request.substring(request.indexOf((":")) + 1, request.length());
      System.out.println("用户上线：" + uname);
      nameList.put(uname, socketChannel);

      //客户端发送的私聊信息  fromName:>toName^msg
    } else if (request.indexOf(":>") != -1) {
      System.out.println("私聊信息" + request);
      String fromeName = request.substring(0, request.indexOf(":>"));
      String toName = request.substring(request.indexOf(":>") + 2, request.indexOf("^"));
      String msg = request.substring(request.indexOf("^") + 1);
      System.out.println("fromName=" + fromeName + "，toName=" + toName + "，msg=" + msg);
      String[] priva = {fromeName, toName, msg};
      privateCast(selector, priva);

      // 否则将客户端发送的请求信息 广播给其他客户端
    } else if (request.length() > 0) {
      // 广播给其他客户端
      broadCast(selector, socketChannel, request);
    }
  }

  /**
   * 私聊发送
   */
  private void privateCast(Selector selector, String[] priva) {
    // 获取到所有已接入的客户端channel
    Set<SelectionKey> selectionKeySet = selector.keys();
    // 循环向所有channel广播信息
    Iterator iterator = selectionKeySet.iterator();

    while (iterator.hasNext()) {
      SelectionKey selectionKey = (SelectionKey) iterator.next();
      Channel targetChannel = selectionKey.channel();
      SocketChannel socketChannel = nameList.get(priva[1]);

      try {
        if (targetChannel != socketChannel) {
          continue;
        } else {
          socketChannel
              .write((Charset.forName("UTF-8").encode("用户" + priva[0] + "发来信息:" + priva[2])));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 广播给其他客户端
   */
  private void broadCast(Selector selector, SocketChannel sourceChannel, String request) {
    // 获取到所有已接入的客户端channel
    Set<SelectionKey> selectionKeySet = selector.keys();
    // 循环向所有channel广播信息
    Iterator iterator = selectionKeySet.iterator();
    while (iterator.hasNext()) {
      SelectionKey selectionKey = (SelectionKey) iterator.next();
      Channel targetChannel = selectionKey.channel();

      //不向服务端自己转发信息
      if ((targetChannel instanceof SocketChannel)) {
        try {
          // 将信息群发给target的客户端
          ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

//  /**
//   * 主程序
//   */
//  public static void main(String[] args) throws IOException {
//
//    Server nioServer = new Server();
//    nioServer.start();
//  }

}
