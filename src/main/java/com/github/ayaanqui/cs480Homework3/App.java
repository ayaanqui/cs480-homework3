package com.github.ayaanqui.cs480Homework3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static Connection establishConnect(String username, String password, String dbName) {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, username, password);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Creates employee and department tables. And creates relations between the two
     * tables
     * 
     * @param conn Active MySQL connection
     */
    public static void createTables(final Connection conn) {
        try (Statement statement = conn.createStatement()) {
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employee" + "(ename VARCHAR(255) NOT NULL,"
                    + " deptName VARCHAR(255) NOT NULL," + " salary DECIMAL(13,2) NOT NULL,"
                    + " city VARCHAR(255) NOT NULL," + " PRIMARY KEY (ename))";
            String createDepartmentTable = "CREATE TABLE IF NOT EXISTS department" + "(deptName VARCHAR(255) NOT NULL,"
                    + " mname VARCHAR(255) NOT NULL," + " PRIMARY KEY (deptName))";

            statement.executeUpdate(createEmployeeTable);
            statement.executeUpdate(createDepartmentTable);
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        String filename = "db_config.json";
        DB.DbConfig dbConfig = new DB(filename).getConfig();
        if (dbConfig == null) {
            System.err.println("File not found or json is invalid. Make sure to read README.md file");
        }

        final Connection conn = establishConnect(dbConfig.username, dbConfig.password, dbConfig.dbName);
        if (conn == null) {
            System.err.printf(
                    "Could not establish connection with DB. Make sure %s has the correct username, password, and database name\n",
                    filename);
            return;
        }

        // Creates employee and department tables
        createTables(conn);
    }
}
