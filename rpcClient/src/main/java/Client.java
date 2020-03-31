import com.manxuan.rpc.RpcClient;
import com.manxuan.rpc.interfaces.test;
import interfaces.People;
import interfaces.User;
import java.io.Serializable;

public class Client {

  public static void main(String[] args) throws InterruptedException {

    //获取RpcClient类，通过此类，可以获取远程服务
    RpcClient rpcClient = new RpcClient();
    Thread.sleep(1000);
    User user=rpcClient.getProxy(User.class);
    People people=rpcClient.getProxy(People.class);

    String result1=user.addUser(20);
    System.out.println("结果是"+result1);

    String result2=people.run("张三");
    System.out.println(result2);

  }
}