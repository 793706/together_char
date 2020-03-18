import com.manxuan.rpc.RpcClient;
import com.manxuan.rpc.interfaces.User;
import com.manxuan.rpc.netty.NettyClient;
import io.netty.channel.Channel;

public class Client {

  public static void main(String[] args) throws Exception{


    RpcClient rpcClient = new RpcClient();

    User user=rpcClient.getProxy(User.class,"127.0.0.1",8080);
    String result=user.addUser(20);

  }
}
