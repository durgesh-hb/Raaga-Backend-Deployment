package com.mymusic.backend;

import javax.sql.DataSource;
import java.sql.Connection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDatabaseConnection(DataSource dataSource) {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				System.out.println("\n=================================================");
				System.out.println("✅ SUCCESS: Connected to Supabase PostgreSQL Database!");
				System.out.println("Database Product Name: " + connection.getMetaData().getDatabaseProductName());
				System.out.println("Database Product Version: " + connection.getMetaData().getDatabaseProductVersion());
				System.out.println("Database Catalog: " + connection.getCatalog());
				System.out.println("=================================================\n");
			} catch (Exception e) {
				System.err.println("\n=================================================");
				System.err.println("❌ ERROR: Failed to connect to Supabase PostgreSQL Database!");
				System.err.println("Error message: " + e.getMessage());
				System.err.println("=================================================\n");
			}
		};
    }}