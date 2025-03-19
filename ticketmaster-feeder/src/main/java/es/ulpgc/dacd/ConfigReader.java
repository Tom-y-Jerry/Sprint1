package es.ulpgc.dacd;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (Exception ignored) {}
    }

    public static String getApiKey(String key) {
        return properties.getProperty(key);
    }
}

