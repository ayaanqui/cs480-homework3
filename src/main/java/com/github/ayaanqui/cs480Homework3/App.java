package com.github.ayaanqui.cs480Homework3;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    private final ClassLoader classLoader;
    private final Connection conn;

    public App(Connection conn) {
        classLoader = getClass().getClassLoader();
        this.conn = conn;
    }

    public static void main(String[] args) {
        final Connection conn = DB.connect(); // Get MySQL connection
        if (conn == null) {
            System.err.printf(
                    "Could not establish connection with DB. Make sure %s has the correct username, password, and database name\n",
                    DB.CONFIG_FILE);
            return;
        }
        DB.createTables(conn); // Creates employee and department tables

        // Start program execution
        App app = new App(conn);
        app.startProgram();

        // Since the program has finished execution close MySQL connection
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startProgram() {
        final String file = "transfile.txt";
        try (final Scanner scanner = new Scanner(new File(classLoader.getResource(file).getFile()))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().trim();
                if (line.charAt(0) == '*')
                    continue;

                // Split line by space into String array
                String[] parsedLine = line.split(" ");
                // Remove empty elements from parsedLine using Array streams
                parsedLine = Arrays.stream(parsedLine).filter(l -> {
                    return !l.trim().equals("");
                }).toArray(String[]::new);

                // Handle command operation
                final PreparedStatement preparedReturn = this.handleCommands(parsedLine);
                if (preparedReturn == null)
                    break;
                preparedReturn.execute();
            }
        } catch (FileNotFoundException e) {
            System.err.printf(
                    "There was a problem reading %s. It might not exist or is already open in another program.\n",
                    file);
        } catch (SQLException e) {
            System.err.println("Could not execute SQL query.");
            System.err.println(e);
        }
    }

    private PreparedStatement handleCommands(final String[] parsedLine) {
        try {
            final int cmd = Integer.parseInt(parsedLine[0]);
            PreparedStatement pstmt = null;
            switch (cmd) {
            case 1:
                // Perform delete on existing employee
                pstmt = this.conn.prepareStatement("DELETE FROM employee WHERE ename = ?");
                pstmt.setString(1, parsedLine[1]);
                return pstmt;
            case 2:
                // Perform insertion on employee order: ename, deptName, salary, city
                pstmt = this.conn
                        .prepareStatement("INSERT INTO employee (ename, deptName, salary, city) VALUES(?, ?, ?, ?)");
                pstmt.setString(1, parsedLine[1]);
                pstmt.setString(2, parsedLine[2]);
                pstmt.setDouble(3, Double.parseDouble(parsedLine[3]));
                pstmt.setString(4, parsedLine[4]);
                return pstmt;
            case 3:
                // Perform delete on existing department tuple
                pstmt = this.conn.prepareStatement("DELETE FROM department WHERE deptName = ?");
                pstmt.setString(1, parsedLine[1]);
                return pstmt;
            case 4:
                // Perform insertion on department order: deptName, mname
                return null;
            case 5:
                return null;
            case 6:
                return null;
            default:
                System.err.println("Unknown command");
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Command type must be a number.");
            return null;
        } catch (SQLException e) {
            System.err.println("Incorrect command params provided.");
            return null;
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Incorrect number of params provided.");
            return null;
        }
    }
}
