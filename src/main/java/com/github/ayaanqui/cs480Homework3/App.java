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
            return null;
        }
    }

    public static void main(String[] args) {
        DB.DbConfig dbConfig = new DB("db_config.json").getConfig();
        if (dbConfig == null) {
            System.err.println("File not found or json is invalid. Make sure to read README.md file");
        }

        Connection c = establishConnect(dbConfig.username, dbConfig.password, dbConfig.dbName);
        if (c == null) {
            System.err.println("Could not establish connection with DB");
        }
        System.out.println(c.toString());
    }
}
