package com.teamtoast.toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.auth.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.FileInputStream;
import java.io.IOException;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
public class Application {

    public static Config config;

    public static void main(String[] args) {
        loadConfig();
        UserController.initAlgorithm();
        SpringApplication.run(Application.class, args);
    }

    private static void loadConfig() {
        try {
            config = new ObjectMapper().readValue(new FileInputStream("config.json"), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
