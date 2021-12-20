import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @program: curator-zk
 * @description: 12306模拟服务器
 * @author: Mr.He
 * @create: 2021-12-20 16:12
 **/
public class Ticket12306 implements Runnable {

    private int tickets = 10; //数据库票数

    private InterProcessMutex lock;

    public Ticket12306(){
        RetryPolicy retryPolicy1 = new ExponentialBackoffRetry(3000, 10);

        CuratorFramework client  = CuratorFrameworkFactory.builder()
                .connectString("121.40.43.57:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy1)
                .build();
        client.start();
        lock = new InterProcessMutex(client,"/lock");
    }

    @Override
    public void run() {
        while (true) {
            //获取锁
            try {
                lock.acquire(3, TimeUnit.SECONDS);
                if(tickets > 0){
                    System.out.println(Thread.currentThread() + " : "+tickets);
                    Thread.sleep(100);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //释放锁
                try{
                    lock.release();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
}
