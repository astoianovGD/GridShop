package com.bobocode;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Main application class.
 */
@SpringBootApplication
@RequiredArgsConstructor
public class Main implements CommandLineRunner {

    /** Session manager for handling console interaction loops. */
    private final ConsoleSessionManager sessionManager;

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(
                "Europe/Kyiv"
        ));

        SpringApplication.run(Main.class, args);
    }

    /**
     * Callback used to run the application.
     *
     * @param args incoming command line arguments
     */
    @Override
    public final void run(final String... args) {
        sessionManager.startSession();
    }
}
