package com.sbvia.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>SbviaApplication class.</p>
 *
 * @author Keitho_
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SbviaApplication {
    /** Default constructor for SbviaApplication. */
    public SbviaApplication() {}

    /**
     * Método público.
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        SpringApplication.run(SbviaApplication.class, args);
    }
}
