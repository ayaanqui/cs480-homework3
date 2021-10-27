package com.github.ayaanqui.cs480Homework3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static Connection establishConnect(String username, String password, String dbName) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, username,
                password)) {
            return conn;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello");
        DB.DbConfig dbConfig = new DB("db_config.json").getConfig();
        Connection c = establishConnect(dbConfig.username, dbConfig.password, dbConfig.dbName);
        if (c == null) {
            System.err.println("Could not establish connection with DB");
        }
        System.out.println(c.toString());
    }
}
