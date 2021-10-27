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

    public DbConfig getConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        try {
            String rawJson = FileUtils.readFileToString(file, "UTF-8");
            Gson gson = new Gson();
            return gson.fromJson(rawJson, DbConfig.class);
        } catch (IOException e) {
            System.err.println(e);
            return null;
        } catch (JsonSyntaxException e) {
            System.err.println(e);
            return null;
        }
    }
}
