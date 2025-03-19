package es.ulpgc.dacd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("No se encontró el archivo config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error al cargar config.properties: " + e.getMessage());
        }
    }

    public static String getApiKey(String key) {
        return properties.getProperty(key);
    }
}
