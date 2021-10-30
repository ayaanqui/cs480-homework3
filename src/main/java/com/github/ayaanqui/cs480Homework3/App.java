package com.github.ayaanqui.cs480Homework3;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
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

                final String[] parsedLine = line.split(" ");
                boolean commandResult = this.handleCommands(parsedLine);
                if (!commandResult)
                    break;
            }
        } catch (FileNotFoundException e) {
            System.err.printf(
                    "There was a problem reading %s. It might not exist or is already open in another program.\n",
                    file);
        }
    }

    private boolean handleCommands(final String[] parsedLine) {
        try {
            final int cmd = Integer.parseInt(parsedLine[0]);
            switch (cmd) {
            case 1:
                // Perform delete on existing employee
                return true;
            case 2:
                // Perform insertion on employee order: ename, deptName, salary, city
                return true;
            case 3:
                // Perform delete on existing department tuple
                return true;
            case 4:
                // Perform insertion on department order: deptName, mname
                return true;
            case 5:
                return true;
            case 6:
                return true;
            default:
                System.err.println("Unknown command");
                return false;
            }
        } catch (NumberFormatException e) {
            System.err.println("Command type must be a number.");
            return false;
        }
    }
}
