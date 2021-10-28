package com.github.ayaanqui.cs480Homework3;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.FileUtils;

public class DB {
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
}
