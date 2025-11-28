package com.example.projectaws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${DB_HOST}")
    private String host;

    @Value("${DB_NAME}")
    private String dbName;

    @PostConstruct
    public void initializeDatabase() {
        String url = "jdbc:mysql://" + host + ":3306/?allowPublicKeyRetrieval=true&useSSL=false";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "`";
            stmt.executeUpdate(sql);
            System.out.println("Base de datos '" + dbName + "' verificada/creada exitosamente.");

        } catch (Exception e) {
            System.err.println("Error al intentar crear la base de datos: " + e.getMessage());
        }
    }
}