package com.CallTaxiService.cucumber.configs;
import java.io.IOException;
import java.util.Properties;
public class ConfigReader {
    private static Properties prop;

    public static Properties initProperties() {
        prop = new Properties();
        try {
        	prop.load(ConfigReader.class.getClassLoader().getResourceAsStream("config.properties"));

        } catch (IOException e) {
            System.out.println("Failed to load config.properties: " + e.getMessage());
        }
        return prop;
    }

    public static String getProperty(String key) {
        if (prop == null) {
            initProperties();
        }
        return prop.getProperty(key);
    }
}