package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Slf4j
@EnableFeignClients
public class DefrayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefrayApplication.class, args);
        log.info("支付服务启动成功....");
    }

}
