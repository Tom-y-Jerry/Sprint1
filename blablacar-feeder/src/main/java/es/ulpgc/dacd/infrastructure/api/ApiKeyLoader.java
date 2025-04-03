package es.ulpgc.dacd.infrastructure.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ApiKeyLoader {
    public static String loadApiKey(String filePath) {
        try {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) {
                System.err.println("❌ El archivo no existe: " + filePath);
                return null;
            }

            if (!Files.isReadable(path)) {
                System.err.println("❌ No se puede leer el archivo: " + filePath);
                return null;
            }

            String key = Files.readAllLines(path).get(0).trim();

            if (key.isEmpty()) {
                System.err.println("❌ El archivo está vacío o mal formateado.");
                return null;
            }

            return key;

        } catch (IOException e) {
            System.err.println("❌ Error leyendo el archivo: " + e.getMessage());
            return null;
        } catch (IndexOutOfBoundsException e) {
            System.err.println("❌ El archivo no contiene ninguna línea.");
            return null;
        }
    }
}

