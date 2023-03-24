package com.fortunebill.gmail.zookeeperlock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ZookeeperDistributedLockTest {

    private static int resource = 0;
    private static String connectString = "192.168.5.205:2181";
    private static String lockName = "seq-";

    public static void main(String[] args) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(50);

        // 一定要单独获取 zookeeper 客户端
        ZooKeeper client = getClient(connectString, lockName);

        for (int i = 0; i < 500; i++) {
            executor.submit(() -> {
                // 将 zookeeper 客户端传入锁对象当中，这样所有锁对象公用同一个 zookeeper 客户端
                ZookeeperDistributedLock distributedLock = new ZookeeperDistributedLock(client, lockName);
                try {
                    // 这里要说明一点是，每个线程都要有一个属于自己的锁对象，共享资源是zookeeper上的节点，具体的过程在锁对象的注释里
                    distributedLock.lock();
                    System.out.println(Thread.currentThread().getName() + " 线程打印: " + ++resource);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // 释放锁，删除当前持有锁的节点
                        distributedLock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        TimeUnit.SECONDS.sleep(30);
        client.close();

    }

    private static ZooKeeper getClient(String connectString, String rootPath) throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connectString, 10000, event -> {
            System.out.println(Thread.currentThread().getName() + " ==> 线程发生了事件: " + event.getState());
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        Stat stat = zooKeeper.exists(rootPath, false);
        if (stat == null) {
            zooKeeper.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        return zooKeeper;
    }

}
