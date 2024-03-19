package com.example.springbootnettyclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootNettyClientApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(SpringBootNettyClientApplication.class, args);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
