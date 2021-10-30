package com.github.ayaanqui.cs480Homework3;

import java.sql.Connection;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        final Connection conn = DB.connect(); // Get MySQL connection
        if (conn == null) {
            System.err.printf(
                    "Could not establish connection with DB. Make sure %s has the correct username, password, and database name\n",
                    DB.CONFIG_FILE);
            return;
        }
        DB.createTables(conn); // Creates employee and department tables

        try {
            conn.close(); // Close active MySQL connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
