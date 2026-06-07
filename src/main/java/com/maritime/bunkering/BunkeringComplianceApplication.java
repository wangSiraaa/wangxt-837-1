package com.maritime.bunkering;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.maritime.bunkering.mapper")
public class BunkeringComplianceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BunkeringComplianceApplication.class, args);
    }
}
