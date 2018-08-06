package com.teamtoast.toast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Database.Init();
        SpringApplication.run(Application.class, args);
    }
}
