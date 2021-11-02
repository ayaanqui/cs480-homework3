package com.github.ayaanqui.cs480Homework3;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.FileUtils;

public class DB {
    protected static final String CONFIG_FILE = "db_config.json";
    private String filename;

    public static class DbConfig {
        public String username;
        public String password;
        public String dbName;
    }

    public DB(String filename) {
        this.filename = filename;
    }

    /**
     * Parses and get the database config details (username, password, and db name)
     * from the file given in the constructor
     * 
     * @return Database connection config details
     */
    public DbConfig getConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        // Load file
        File file = new File(classLoader.getResource(filename).getFile());
        try {
            // Convert file contents into string
            String rawJson = FileUtils.readFileToString(file, "UTF-8");

            // Parse rawJson string into JSON object mapped to DbConfig class
            Gson gson = new Gson();
            return gson.fromJson(rawJson, DbConfig.class);
        } catch (IOException e) {
            return null;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * Connects to a MySQL database with config details from
     * <code>db_config.json</code> file
     * 
     * @return Acitve connection to database
     */
    protected static Connection connect() {
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
    protected static Connection establishConnect(String username, String password, String dbName) {
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
    protected static void createTables(final Connection conn) {
        try (Statement statement = conn.createStatement()) {
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employee (ename VARCHAR(255) NOT NULL, deptName VARCHAR(255), salary DECIMAL(13,2) NOT NULL, city VARCHAR(255) NOT NULL, PRIMARY KEY (ename))";
            String createDepartmentTable = "CREATE TABLE IF NOT EXISTS department (deptName VARCHAR(255) NOT NULL, mname VARCHAR(255) NOT NULL, PRIMARY KEY (deptName), FOREIGN KEY (mname) REFERENCES employee(ename) ON DELETE CASCADE ON UPDATE CASCADE)";

            // Create employee table
            statement.executeUpdate(createEmployeeTable);
            // Create department table with foreign key refrencing employee.ename
            statement.executeUpdate(createDepartmentTable);
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    protected static void dropAllTables(final Connection conn) {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS department");
            statement.executeUpdate("DROP TABLE IF EXISTS employee");
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
}
