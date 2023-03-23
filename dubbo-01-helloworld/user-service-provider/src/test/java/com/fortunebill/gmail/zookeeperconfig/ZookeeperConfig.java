package com.fortunebill.gmail.zookeeperconfig;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * 基于 zookeeper 做配置中心管理的简单demo
 *
 * @author Kavin
 * @date 2023年03月23日 9:26
 */
public class ZookeeperConfig {

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ZooKeeperClient zooKeeperClient = new ZooKeeperClient();
        zooKeeperClient.connect();
        ConfigManager configManager = new ConfigManager(zooKeeperClient);


        // 添加配置
        configManager.addConfig("key1", "value1");
        configManager.addConfig("key2", "value2");
        configManager.addConfig("key3", "value3");

        // 获取配置
        String value1 = configManager.getConfig("key1");
        String value2 = configManager.getConfig("key2");
        String value3 = configManager.getConfig("key3");
        System.out.println("value1: " + value1);
        System.out.println("value2: " + value2);
        System.out.println("value3: " + value3);
        //
        // 修改配置
        configManager.updateConfig("key1", "new_value1");
        String new_value1 = configManager.getConfig("key1");
        System.out.println("new_value1: " + new_value1);

        // 删除配置
        configManager.deleteConfig("key2");
        String deleted_value2 = configManager.getConfig("key2");
        System.out.println("deleted_value2: " + deleted_value2);

        zooKeeperClient.close();
    }
}

