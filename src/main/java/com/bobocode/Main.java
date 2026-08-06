package com.bobocode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Main application class.
 */
@SpringBootApplication
public class Main {

    /**
     * Protected constructor to hide
     * the implicit public one for utility/main classes
     * while allowing Spring/CGLIB proxying.
     */
    protected Main() {
    }

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        TimeZone.setDefault(TimeZone
                .getTimeZone("Europe/Kyiv")
        );
        SpringApplication.run(Main.class, args);
    }
}
