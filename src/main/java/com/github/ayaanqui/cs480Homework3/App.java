package com.github.ayaanqui.cs480Homework3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    private static final String CONFIG_FILE = "db_config.json";

    public static void main(String[] args) {
        final Connection conn = connect(); // Get MySQL connection
        if (conn == null) {
            System.err.printf(
                    "Could not establish connection with DB. Make sure %s has the correct username, password, and database name\n",
                    CONFIG_FILE);
            return;
        }
        createTables(conn); // Creates employee and department tables

        try {
            conn.close(); // Close active MySQL connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to a MySQL database with config details from
     * <code>db_config.json</code> file
     * 
     * @return Acitve connection to database
     */
    private static Connection connect() {
        DB.DbConfig dbConfig = new DB(CONFIG_FILE).getConfig();
        if (dbConfig == null) {
            System.err.println("File not found or json is invalid. Make sure to read README.md file");
        }
        return establishConnect(dbConfig.username, dbConfig.password, dbConfig.dbName);
    }

    /**
     * Connects to a MySQL database with a username, password, and dbname
     * 
     * @param username Database username
     * @param password Database password
     * @param dbName   Database schema name
     * @return Acitve connection to database
     */
    private static Connection establishConnect(String username, String password, String dbName) {
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
    private static void createTables(final Connection conn) {
        try (Statement statement = conn.createStatement()) {
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employee" + "(ename VARCHAR(255) NOT NULL,"
                    + " deptName VARCHAR(255) NOT NULL," + " salary DECIMAL(13,2) NOT NULL,"
                    + " city VARCHAR(255) NOT NULL," + " PRIMARY KEY (ename))";
            String createDepartmentTable = "CREATE TABLE IF NOT EXISTS department" + "(deptName VARCHAR(255) NOT NULL,"
                    + " mname VARCHAR(255) NOT NULL," + " PRIMARY KEY (deptName))";

            statement.executeUpdate(createEmployeeTable);
            statement.executeUpdate(createDepartmentTable);
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
}
