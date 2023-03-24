package com.fortunebill.gmail.curatorlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * InterProcessMutex Demo: 分布式可重入互斥锁
 */
public class InterProcessMutexDemo {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static String zkLockPath = "/Aaron/Lock1";

    private static int count = 0;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // 创建客户端
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                                                           .connectString("192.168.5.205:2181")    // ZK Server地址信息
                                                           .connectionTimeoutMs(15 * 1000) // 连接超时时间: 15s
                                                           .sessionTimeoutMs(60 * 1000)  // 会话超时时间: 60s
                                                           .retryPolicy(new RetryNTimes(3, 1000))// 重试策略: 重试3次, 每次间隔1s
                                                           .build();
        // 启动客户端
        zkClient.start();
        System.out.println("---------------------- 系统上线 ----------------------");

        for (int i = 1; i <= 1000; i++) {
            String taskName = "任务#" + i;
            Task task = new Task(taskName, zkClient, zkLockPath);
            threadPool.execute(task);
        }

        // 主线程等待所有任务执行完毕
        try {
            Thread.sleep(120 * 1000);
        } catch (Exception e) {
        }
        // 关闭客户端
        zkClient.close();
        System.out.println("---------------------- 系统下线 ----------------------");
    }

    /**
     * 打印信息
     *
     * @param msg
     */
    private static void info(String msg) {
        String time = formatter.format(LocalTime.now());
        String thread = Thread.currentThread().getName();
        String log = "[" + time + "] " + " <" + thread + "> " + msg;
        System.out.println(log);
    }

    private static class Task implements Runnable {
        private String taskName;

        private InterProcessMutex lock;

        public Task(String taskName, CuratorFramework zkClient, String zkLockPath) {
            this.taskName = taskName;
            this.lock = new InterProcessMutex(zkClient, zkLockPath);
        }

        @Override
        public void run() {
            try {
                lock.acquire();
                info(taskName + ": 成功获取锁 #1");
                // 模拟业务耗时
                System.out.println("打印了: " + count++);
                methodA();
            } catch (Exception e) {
                System.out.println(taskName + ": Happen Exception: " + e.getMessage());
            } finally {
                info(taskName + ": 释放锁 #1\n");
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void methodA() {
            try {
                lock.acquire();
                info(taskName + ": 成功获取锁 #2");

                // 模拟业务耗时
                // Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(taskName + ": Happen Exception: " + e.getMessage());
            } finally {
                info(taskName + ": 释放锁 #2");
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}