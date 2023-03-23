package com.fortunebill.gmail.zookeeperconfig;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String ROOT_PATH = "/config";
    private ZooKeeperClient zooKeeperClient;
    private Map<String, String> configMap = new HashMap<>();

    public ConfigManager(ZooKeeperClient zooKeeperClient) throws KeeperException, InterruptedException {
        this.zooKeeperClient = zooKeeperClient;
        init();
    }

    private void init() throws KeeperException, InterruptedException {
        if (zooKeeperClient.getZooKeeper().exists(ROOT_PATH, false) == null) {
            zooKeeperClient.getZooKeeper().create(ROOT_PATH, "".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        for (String node : zooKeeperClient.getZooKeeper().getChildren(ROOT_PATH, false)) {
            byte[] data = zooKeeperClient.getZooKeeper().getData(ROOT_PATH + "/" + node, false, null);
            configMap.put(node, new String(data, StandardCharsets.UTF_8));
        }
    }

    public void addConfig(String key, String value) throws KeeperException, InterruptedException {
        if (zooKeeperClient.getZooKeeper().exists(ROOT_PATH + "/" + key, false) == null) {
            zooKeeperClient.getZooKeeper().create(ROOT_PATH + "/" + key, value.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            configMap.put(key, value);
        }
    }

    public void updateConfig(String key, String value) throws KeeperException, InterruptedException {
        if (zooKeeperClient.getZooKeeper().exists(ROOT_PATH + "/" + key, false) != null) {
            zooKeeperClient.getZooKeeper().setData(ROOT_PATH + "/" + key, value.getBytes(StandardCharsets.UTF_8), -1);
            configMap.put(key, value);
        }
    }

    public void deleteConfig(String key) throws KeeperException, InterruptedException {
        if (zooKeeperClient.getZooKeeper().exists(ROOT_PATH + "/" + key, false) != null) {
            zooKeeperClient.getZooKeeper().delete(ROOT_PATH + "/" + key, -1);
            configMap.remove(key);
        }
    }

    public String getConfig(String key) {
        return configMap.get(key);
    }
}
