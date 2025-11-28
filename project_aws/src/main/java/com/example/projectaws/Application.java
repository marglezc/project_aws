package com.example.projectaws;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            System.out.println("Variables .env cargadas.");
        } catch (Exception e) {
            System.out.println("No se encontró .env.");
        }

        SpringApplication.run(Application.class, args);
    }
}