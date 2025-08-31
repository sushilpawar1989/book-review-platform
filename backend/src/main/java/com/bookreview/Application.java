package com.bookreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Book Review Platform.
 * 
 * @author Book Review Platform Team
 * @version 0.1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class Application {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
