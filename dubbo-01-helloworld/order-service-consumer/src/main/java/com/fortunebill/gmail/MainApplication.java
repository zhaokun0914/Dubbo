package com.fortunebill.gmail;

import com.fortunebill.gmail.bean.UserAddress;
import com.fortunebill.gmail.service.OrderService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;

/**
 * @author Kavin
 * @date 2021年12月07日 23:13
 */
public class MainApplication {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("consumer.xml");

        OrderService bean = ioc.getBean(OrderService.class);
        List<UserAddress> userAddresses = bean.initOrder("123");
        System.out.println(userAddresses);
        System.out.println("调用完成");

        System.in.read();
    }


}
