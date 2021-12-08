package com.fortunebill.gmail.service.impl;

import com.fortunebill.gmail.bean.UserAddress;
import com.fortunebill.gmail.service.OrderService;
import com.fortunebill.gmail.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 1、将服务提供者注册到注册中心
 *      1、导入dubbo依赖（2.6.2）、操作zookeeper的客户端(curator-client)
 *      2、配置服务提供者
 * 2、让消费者去注册中心订阅服务提供者的地址
 *
 * @author Kavin
 * @date 2021年12月07日 21:25
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;

    @Override
    public List<UserAddress> initOrder(String userId) {
        System.out.println("用户ID："+ userId);
        // 1.查询用户的收货地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        return userAddressList;
    }
}
