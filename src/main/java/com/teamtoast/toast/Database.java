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

    /**
     * 새로운 DB 커넥션 생성.
     * @return Connection
     * @throws SQLException
     */
    public static Connection newConnection() throws SQLException {
         //return DriverManager.getConnection("jdbc:mysql://" + config.host + ":" + config.port + "/" + config.database,
           //     config.user, config.password);
         return DriverManager.getConnection("jdbc:mariadb://" + Application.config.database.host + ":" + Application.config.database.port + "/" + Application.config.database.name,
                 Application.config.database.user, Application.config.database.password);
    }

}
