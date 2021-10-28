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
        String filename = "db_config1.json";
        DB.DbConfig dbConfig = new DB(filename).getConfig();
        if (dbConfig == null) {
            System.err.println("File not found or json is invalid. Make sure to read README.md file");
        }

        Connection c = establishConnect(dbConfig.username, dbConfig.password, dbConfig.dbName);
        if (c == null) {
            System.err.printf(
                    "Could not establish connection with DB. Make sure %s has the username, password, and database name",
                    filename);
        }
        System.out.println(c.toString());
    }
}
