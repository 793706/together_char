import com.manxuan.rpc.RpcClient;
import com.manxuan.rpc.interfaces.test;
import interfaces.People;
import interfaces.User;
import java.io.Serializable;

public class Client {

  public static void main(String[] args){

    RpcClient rpcClient = new RpcClient();
    User user=rpcClient.getProxy(User.class);
    People people=rpcClient.getProxy(People.class);

    String result=user.addUser(20);

    System.out.println("结果是"+result);
    System.out.println(people.run("张三"));
    System.out.println("!!!!!");
  }
}