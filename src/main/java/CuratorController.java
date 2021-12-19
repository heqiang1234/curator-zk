import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author HQ
 * @program: curator-zk
 * @description: 测试   zk客户端
 * @date 2021-12-19 14:17:22
 */
public class CuratorController {

    public static CuratorFramework client = null;

    public static void connectLink(){

        //第一种方式
        /**
         * @param String connectString list of server to connect to 连接字符串。ZK server 地址，端口  172.26.2.186:2181
         * @param int sessionTimeoutMs session timeout 会话超时时间 单位ms
         * @param int connectionTimeoutMs connection timeout 连接超时时间 单位ms
         * @param RetryPolicy retryPolicy retry policy to use 重试策略
         */
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
        client = CuratorFrameworkFactory.newClient("172.26.2.186:2181",
                60 * 1000, 15 * 1000, retryPolicy);
        client.start();
    }

    //创建节点
    public void createNode(){

    }

    public static void main(String[] args) {
        connectLink();
    }
}
