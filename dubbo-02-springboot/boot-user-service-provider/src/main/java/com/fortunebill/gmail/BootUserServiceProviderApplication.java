package com.fortunebill.gmail;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;

/**
 * 1、导入依赖
 *  1)、导入dubbo-starter
 *  2)、导入dubbo的其他依赖
 *
 * 2、属性的优先级顺序
 *  1）、虚拟机配置-Ddubbo.protocol.port=20880
 *  2）、application.yml
 *  3）、dubbo.porperties（dubbo公共的配置）
 *
 * 3、springboot与dubbo整合的三种方式
 *  1）、导入dubbo-starter，在application.yml中配置属性，使用@Service暴露服务，使用@Reference引用服务
 *  2）、使用dubbo xml配置文件的方式：导入dubbo-starter、使用@ImportResource导入配置文件即可
 *  3）、使用@Configuration将各个组件编码的方式设置进去
 */
@SpringBootApplication
@EnableDubbo     // 开启基于注解的dubbo
@EnableHystrix   // 开启服务容错
public class BootUserServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootUserServiceProviderApplication.class, args);
    }

}
