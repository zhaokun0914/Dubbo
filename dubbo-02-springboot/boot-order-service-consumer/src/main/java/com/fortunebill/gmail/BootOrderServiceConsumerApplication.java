package com.fortunebill.gmail;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 启动时检查：
 *     生产者服务启动时检查(@Reference(check = true))：Dubbo 默认会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，默认 check="true"
 *     注册中心启动时检查(dubbo.registry.check)：如果有注册中心启动正常，如果没有注册中心启动报错
 * 服务超时：
 *     @Reference(timeout = 3000)：单位毫秒，默认1000毫秒
 * 重试次数：
 *     @Reference(retries = 3)：重试次数，如果失败重试3次
 *
 * 配置的覆盖关系
 *   总则：方法级优先、接口次之、全局配置再次之。（精确优先）
 *        如果级别一样，则消费方优先，提供方次之（消费优先于生产）
 *
 */
@SpringBootApplication
@EnableDubbo // 开启基于注解的dubbo
@EnableHystrix
public class BootOrderServiceConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootOrderServiceConsumerApplication.class, args);
    }

}
