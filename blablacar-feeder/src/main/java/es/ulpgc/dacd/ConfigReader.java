package es.ulpgc.dacd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    public static String getApiKey(String keyName) {
        Properties props = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new RuntimeException("No se encontró config.properties");
            props.load(input);
            return props.getProperty(keyName);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer config.properties", e);
        }
    }
}
