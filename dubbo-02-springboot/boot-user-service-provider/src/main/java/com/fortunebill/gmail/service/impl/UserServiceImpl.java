package com.fortunebill.gmail.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fortunebill.gmail.bean.UserAddress;
import com.fortunebill.gmail.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Service(version = "1.0.0")
@Component("userServiceImpl01")
public class UserServiceImpl implements UserService {

    @HystrixCommand // 如果这个服务出现异常就会容错
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("UserServiceImpl.....old version...");

        UserAddress address1 = new UserAddress(1, "北京市昌平区宏福科技园综合楼3层", "1", "李老师", "010-56253825", "Y");
        UserAddress address2 = new UserAddress(2, "深圳市宝安区西部硅谷大厦B座3层（深圳分校）", "1", "王老师", "010-56253825", "N");

        // 睡4秒，模拟服务超时，测试重试次数
        // try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
        if (Math.random() > 0.5) {
            throw new RuntimeException("==> 发生了随机异常");
        }

        return Arrays.asList(address1, address2);
    }

}
