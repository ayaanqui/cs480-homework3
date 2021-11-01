package com.github.ayaanqui.cs480Homework3;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                // Ignore lines that start with a * or #
                if (line.charAt(0) == '*' || line.charAt(0) == '#' || line.isEmpty())
                    continue;

                // Split line by space into String array
                String[] parsedLine = line.split(" ");
                // Remove empty elements from parsedLine using Array streams
                parsedLine = Arrays.stream(parsedLine).filter(l -> {
                    return !l.trim().equals("");
                }).toArray(String[]::new);

                // Handle command operation
                final PreparedStatement preparedReturn = this.handleCommands(parsedLine);
                if (preparedReturn != null)
                    preparedReturn.execute();
            }
        } catch (FileNotFoundException e) {
            System.err.printf(
                    "There was a problem reading %s. It might not exist or is already open in another program.\n",
                    file);
        } catch (SQLException e) {
            System.err.println("Could not: execute SQL query.");
            System.err.println(e);
        }
    }

    private PreparedStatement handleCommands(final String[] parsedLine) {
        try {
            final int cmd = Integer.parseInt(parsedLine[0]);
            PreparedStatement prep = null;
            String deptName, ename, mname, city, salary;
            switch (cmd) {
            case 1:
                // Perform delete on existing employee
                // Check to see if the ename exists
                ename = parsedLine[1];

                if (!employeeExists(parsedLine[1])) {
                    System.out.println("Not found");
                    return null;
                }

                prep = this.conn.prepareStatement("DELETE FROM employee WHERE ename = ?");
                prep.setString(1, ename);
                System.out.println("Deleted");
                return prep;
            case 2:
                // Perform insertion on employee order: ename, deptName, salary, city
                // Before insertion check if ename already exists in the db
                ename = parsedLine[1];
                deptName = parsedLine[2];
                salary = parsedLine[3];
                city = parsedLine[4];

                if (employeeExists(ename)) {
                    System.out.println("Duplicate name");
                    return null;
                }

                // Insert details into database
                prep = this.conn
                        .prepareStatement("INSERT INTO employee (ename, deptName, salary, city) VALUES(?, ?, ?, ?)");
                prep.setString(1, ename);
                prep.setString(2, deptName);
                prep.setDouble(3, Double.parseDouble(salary));
                prep.setString(4, city);
                System.out.println("Added");
                return prep;
            case 3:
                // Perform delete on existing department tuple
                deptName = parsedLine[1];

                if (!this.departmentExists(deptName)) {
                    System.out.println("Department not found");
                    return null;
                }

                // Set all employee with the department name as deptName to NULL
                PreparedStatement updateEmployees = this.conn
                        .prepareStatement("UPDATE employee SET deptName = NULL WHERE deptName = ?");
                updateEmployees.setString(1, deptName);
                updateEmployees.executeUpdate();

                prep = this.conn.prepareStatement("DELETE FROM department WHERE deptName = ?");
                prep.setString(1, deptName);
                return prep;
            case 4:
                // Perform insertion on department order: deptName, mname
                deptName = parsedLine[1];
                mname = parsedLine[2];

                // Check if manager name exists in the employee table
                if (!employeeExists(mname)) {
                    System.out.println("Employee does not exist");
                    return null;
                }

                prep = this.conn.prepareStatement("INSERT INTO department (deptName, mname) VALUES(?, ?)");
                prep.setString(1, deptName);
                prep.setString(2, mname);
                System.out.println("Department added");
                return prep;
            case 5:
                // List names of all employees who work directly or indirectly for a given
                // manager
                mname = parsedLine[1];

                prep = this.conn.prepareStatement(
                        "SELECT ename FROM department AS `d` INNER JOIN employee `e` ON e.mname = d.ename WHERE mname = ?");
                prep.setString(1, parsedLine[1]);
                ResultSet result = prep.executeQuery();
                boolean hasNext = result.next();
                while (hasNext) {
                    result.getString("ename");
                    hasNext = result.next();
                }
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

    private boolean employeeExists(String ename) throws SQLException {
        PreparedStatement prep = this.conn.prepareStatement("SELECT COUNT(ename) FROM employee WHERE ename = ?");
        prep.setString(1, ename);
        ResultSet enameSet = prep.executeQuery();
        // If enameSet column is not 0 then employee exists
        if (enameSet.next() && enameSet.getInt(1) >= 1)
            return true;
        return false;
    }

    private boolean departmentExists(String deptName) throws SQLException {
        PreparedStatement prep = this.conn
                .prepareStatement("SELECT COUNT(deptName) FROM department WHERE deptName = ?");
        prep.setString(1, deptName);
        ResultSet deptNameSet = prep.executeQuery();
        // If enameSet column is not 0 then employee exists
        if (deptNameSet.next() && deptNameSet.getInt(1) >= 1)
            return true;
        return false;
    }
}
