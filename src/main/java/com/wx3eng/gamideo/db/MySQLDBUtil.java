package com.wx3eng.gamideo.db;

import com.wx3eng.gamideo.external.TwitchClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

public class MySQLDBUtil {
    private static final String INSTANCE = "gamideo-instance.c6pfean6ifaz.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    private static final String DB_NAME = "gamideo";

    public static String getMySQLAddress() throws IOException {
        Properties prop = new Properties();
        String propFileName = "configTwitch.properties";
        InputStream inputStream = MySQLDBUtil.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        String username = prop.getProperty("user");
        String password = prop.getProperty("password");
        try {
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&serverTimezone=UTC&createDatabaseIfNotExist=true",INSTANCE,PORT_NUM,DB_NAME,username,password);
    }
}
