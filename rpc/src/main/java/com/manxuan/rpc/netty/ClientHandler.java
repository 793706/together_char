package com.manxuan.rpc.netty;

import com.manxuan.rpc.netty.util.RpcRequest;
import com.manxuan.rpc.netty.util.RpcResponse;
import com.manxuan.rpc.util.MsgMap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;


public class ClientHandler extends ChannelDuplexHandler {

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    System.out.println("Clientwrite"+msg.toString());
    if(msg instanceof RpcRequest){
      RpcRequest request=(RpcRequest)msg;
      MsgMap.requestMap.put(request.getRequestId(),null);
    }
    super.write(ctx, msg, promise);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("Clientread"+msg.toString());
    if(msg instanceof RpcResponse){
      RpcResponse response=(RpcResponse)msg;
      MsgMap.requestMap.put(response.getRequestId(),response);
    }
    super.channelRead(ctx, msg);
  }

  public Object getResponse(String key){
      Object object=MsgMap.requestMap.get(key);
      MsgMap.requestMap.remove(key);
      return object;
  }
}
