package com.teamtoast.toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

    public DatabaseConfig database;
    @JsonProperty("token_secret")
    public String tokenSecret;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabaseConfig {

        public String host;
        public int port;
        public String name;
        public String user;
        public String password;

    }

}
