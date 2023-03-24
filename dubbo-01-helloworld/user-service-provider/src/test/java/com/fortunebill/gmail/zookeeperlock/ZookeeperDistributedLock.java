package com.fortunebill.gmail.zookeeperlock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperDistributedLock {
    private final String threadName = Thread.currentThread().getName();
    private final ZooKeeper zooKeeper;
    private final String rootNode = "/locks";
    private final String lockName;
    private String lockPath;
    private String waitPath;
    private final CountDownLatch waitLatch = new CountDownLatch(1);

    public ZookeeperDistributedLock(ZooKeeper zooKeeper, String lockName) {
        this.lockName = lockName;
        this.zooKeeper = zooKeeper;
    }

    public void lock() throws KeeperException, InterruptedException {
        // 1、在 rootNode 下创建一个临时节点 /locks/seq-0000000003
        lockPath = zooKeeper.create(rootNode + "/" + lockName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // 2、获取 rootNode 下所有的[临时节点的名字]，并按从小到大的顺序排序
        List<String> children = zooKeeper.getChildren(rootNode, false);
        Collections.sort(children);

        // 3、获取[当前节点的名字] seq-0000000003
        String substring = lockPath.substring(rootNode.length() + 1);
        // 4、通过[当前节点的名字]找到在所有临时节点当中的位置
        int index = children.indexOf(substring);

        // 5、如果是第一个，那么表示获取到了锁，可以直接执行任务
        if (index == 0) {
            return;
        } else {
            // 6、否则就监听当前节点的前一个节点，如果前一个节点被删除了，那么就唤醒当前节点
            // 6.1 获取[前一个节点]的路径 /locks/seq-0000000002
            waitPath = rootNode + "/" + children.get(index - 1);

            // 6.2 通过 exists 命令判断前一个节点是否存在，如果前一个节点存在就将 watch 绑定到前一个节点上
            Stat stat = zooKeeper.exists(waitPath, event -> {
                System.out.println(threadName + " 线程发生了事件:  " + event.getState());
                // 6.4 监听前一个节点的删除事件
                if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                    System.out.println(Thread.currentThread().getName() + " "+ lockPath + " 监听到: " + event.getPath() + " 被删除，唤醒" + lockPath);
                    // 6.5 监测到前一个节点被删除，唤醒当前节点，这里唤醒的就是 6.3 被阻塞节点的线程
                    waitLatch.countDown();
                }
            });

            if (stat != null) {
                // 6.3 这里是一个重点，如果前一个节点存在，那么就阻塞当前节点的线程
                waitLatch.await();
                System.out.println(threadName + " 获得锁");
            }
        }
    }

    public void unlock() throws KeeperException, InterruptedException {
        System.out.println(threadName + " 释放锁");
        System.out.println(threadName + " 线程节点: " + lockPath + " 被删除");
        System.out.println();
        // 7 删除当前节点，同时会有一个当前节点被删除事件被传播出去，如果当前节点有 watch 的话，就会把事件传入到 watch 中，可以在 watch 中根据感兴趣的事件来处理不同的逻辑
        zooKeeper.delete(lockPath, -1);
    }

}
