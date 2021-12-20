import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author HQ
 * @program: curator-zk
 * @description: 客户端测试
 * @date 2021-12-19 19:30:11
 */
public class cutatorTest {

    public CuratorFramework client = null;

    @Before
    public void connectLink() {

        //第一种方式
        /**
         * @param String connectString list of server to connect to 连接字符串。ZK server 地址，端口  172.26.2.186:2181
         * @param int sessionTimeoutMs session timeout 会话超时时间 单位ms
         * @param int connectionTimeoutMs connection timeout 连接超时时间 单位ms
         * @param RetryPolicy retryPolicy retry policy to use 重试策略
         */
        //重试策略
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
//        client = CuratorFrameworkFactory.newClient("172.26.2.186:2181",
//                60 * 1000, 15 * 1000, retryPolicy);


        //第二种方式
        RetryPolicy retryPolicy1 = new ExponentialBackoffRetry(3000, 10);

        client = CuratorFrameworkFactory.builder()
                .connectString("121.40.43.57:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy1)
                .namespace("testzk")
                .build();
        client.start();
    }

    @Test
    public void testCreate() throws Exception {
        //1. 基本创建
        String path = client.create().forPath("/app1/p1");
        System.out.println(path);
    }

    @Test
    public void testSearch() throws Exception {
        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat);
        System.out.println(new String(data));

        List<String> strings = client.getChildren().forPath("/app1");
        System.out.println(strings);
    }

    @Test
    public void testNddeCache() throws Exception {
        //创建NodeCache对象
        NodeCache nodeCache = new NodeCache(client,"/app1");
        //注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点发生变化");
                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });

        //3.开启监听，如果设置为true，则开启监听加载缓冲数据
        nodeCache.start(true);

        while (true){

        }
    }

    @Test
    public void testPatchChildCache() throws Exception {
        //创建监听
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/app2",true);
        //绑定监听
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                System.out.println("子节点变化了");
                System.out.println(pathChildrenCacheEvent);

                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("数据变化了");
                    byte[] data = pathChildrenCacheEvent.getData().getData();
                    System.out.println(new String(data));
                }

            }
        });
        //开启
        pathChildrenCache.start();

        while (true){

        }
    }

    @Test
    public void testTreeCache() throws Exception {
        //1. 创建监听器
        TreeCache treeCache = new TreeCache(client,"/app2");

        //注册监听
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                System.out.println("子节点变化了");
                System.out.println(treeCacheEvent);
            }
        });

        //InterProcessSemaphoreMutex
//        InterProcessMutex

        //开启
        treeCache.start();

        while (true){

        }
    }


    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
