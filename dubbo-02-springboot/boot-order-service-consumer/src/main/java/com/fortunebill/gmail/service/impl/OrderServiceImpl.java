package com.fortunebill.gmail.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fortunebill.gmail.bean.UserAddress;
import com.fortunebill.gmail.service.OrderService;
import com.fortunebill.gmail.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 1、将服务提供者注册到注册中心
 * 1、导入dubbo依赖（2.6.2）、操作zookeeper的客户端(curator-client)
 * 2、配置服务提供者
 * 2、让消费者去注册中心订阅服务提供者的地址
 *
 * @author Kavin
 * @date 2021年12月07日 21:25
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Reference(
            // 启动时检查关闭，启动时不检查服务提供者是否存在
            check = false,
            // 服务超时时间，默认1000毫秒
            timeout = 1000,
            // 重试次数，如果失败重试3次，我们一般在幂等操作（查询、删除、修改）上设置重复次数，对于非幂等的方法不能设置重试次数
            retries = 3,
            // 多版本功能：*表示随即挑选一个版本进行调用
            version = "1.0.0",
            // 本地存根
            stub = "com.fortunebill.gmail.service.impl.UserServiceStub",
            // 服务直连，跳过注册中心
            //url = "dubbo://127.0.0.1:20880",
            loadbalance = "roundrobin",
            // 集群容错，实际在开发中整合Hystrix
            cluster = ""
    )
    private UserService userService;

    @Override
    @HystrixCommand(fallbackMethod = "hello")
    public List<UserAddress> initOrder(String userId) {
        System.out.println("用户ID：" + userId);
        // 1.查询用户的收货地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        return userAddressList;
    }

    public List<UserAddress> hello(String userId) {
        System.out.println("出错啦~" + userId);
        // 1.查询用户的收货地址
        return Collections.singletonList(new UserAddress(10, "测试地址", "1", "测试", "1", "Y"));
    }
}
