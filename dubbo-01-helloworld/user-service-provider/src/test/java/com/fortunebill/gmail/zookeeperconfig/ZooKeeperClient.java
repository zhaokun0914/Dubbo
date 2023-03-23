package com.fortunebill.gmail.zookeeperconfig;

import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperClient {
    private static final int SESSION_TIMEOUT = 5000;
    private static final String CONNECT_STRING = "192.168.5.205:2181";
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private ZooKeeper zooKeeper;

    public void connect() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, event -> {
            if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        });
        connectedSemaphore.await();
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
}
