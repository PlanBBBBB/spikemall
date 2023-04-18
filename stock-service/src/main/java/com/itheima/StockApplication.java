package com.itheima;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StockApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
        log.info("库存服务启动成功....");
    }

}
