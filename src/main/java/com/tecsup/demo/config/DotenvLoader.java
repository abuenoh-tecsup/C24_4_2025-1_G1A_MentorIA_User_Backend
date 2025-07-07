package com.tecsup.demo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DotenvLoader {
    public static void loadEnv() {
        try (Stream<String> lines = Files.lines(Paths.get(".env"))) {
            lines.filter(line -> line.contains("=")).forEach(line -> {
                String[] parts = line.split("=", 2);
                System.setProperty(parts[0].trim(), parts[1].trim());
            });
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo .env");
        }
    }
}

