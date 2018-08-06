package com.teamtoast.toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB 관리 클래스
 */
public class Database {

    private static Config config;

    /**
     * DB를 database.json 파일로부터 불러옴
     */
    public static void Init() {
        try {
            config = new ObjectMapper().readValue(new FileInputStream("database.json"), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 새로운 DB 커넥션 생성.
     * @return Connection
     * @throws SQLException
     */
    public static Connection newConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mariadb://" + config.host + ":" + config.port + "/" + config.database,
                config.user, config.password);
    }

    /**
     * DB 설정 클래스
     */
    public static class Config {

        public String host;
        public int port;
        public String database;
        public String user;
        public String password;

    }

}
