package com.sbvia.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point that bootstraps the SBVIA Spring Boot application.
 *
 * @author Keitho_
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SbviaApplication {
    /**
     * Boots the SBVIA Spring Boot application by starting the embedded server
     * and initializing the whole application context, including the scheduled
     * and asynchronous task support enabled at class level.
     *
     * @param args command line arguments passed to the application at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(SbviaApplication.class, args);
    }
}
