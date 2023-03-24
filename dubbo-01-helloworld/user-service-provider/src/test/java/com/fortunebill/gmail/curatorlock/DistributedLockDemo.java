package com.fortunebill.gmail.curatorlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedLockDemo {

    private static final String ZK_ADDRESS = "192.168.5.205:2181";
    private static final String LOCK_PATH = "/my-lock";
    private static final int MAX_RETRIES = 5;
    private static int resource = 1;

    public static void main(String[] args) {
        // 创建Curator客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(MAX_RETRIES, 1000));
        client.start();

        // 尝试获取锁
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 2000; i++) {
            executor.execute(() -> {
                // 创建分布式锁
                InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH);
                try {
                    lock.acquire();
                    // 执行需要加锁的业务逻辑
                    System.out.println("获取到锁，执行业务逻辑" + resource++);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 释放锁
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // 主线程等待所有任务执行完毕
        try {
            Thread.sleep(120 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 关闭Curator客户端
        client.close();
    }

}
