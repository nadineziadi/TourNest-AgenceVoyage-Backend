package com.example.hebergement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient

@EnableFeignClients(basePackages = "com.example.hebergement.services")
//@ComponentScan(basePackages = "com.example.hebergement")
public class HebergementApplication {

    public static void main(String[] args) { SpringApplication.run(HebergementApplication.class, args); }

}

