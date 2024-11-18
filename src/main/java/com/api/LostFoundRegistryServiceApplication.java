package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.api.client")
public class LostFoundRegistryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostFoundRegistryServiceApplication.class, args);
    }
}
