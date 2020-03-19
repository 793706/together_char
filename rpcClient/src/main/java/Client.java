import com.manxuan.rpc.RpcClient;
import inter_face.User;
import java.sql.SQLOutput;


public class Client {

  public static void main(String[] args) throws Exception{


    RpcClient rpcClient = new RpcClient("10.168.1.118",8080);


    User user=rpcClient.getProxy(User.class);
    String result=user.addUser(20);
    System.out.println("---*----"+result);

  }
}
