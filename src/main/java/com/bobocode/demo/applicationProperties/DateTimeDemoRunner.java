package com.bobocode.demo.applicationProperties;

import com.bobocode.configs.DateTimeProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Demo runner for displaying configuration properties related to date and time.
 */
@Component
public final class DateTimeDemoRunner implements CommandLineRunner {

    /** Properties containing launch date, time,
     * timestamp, and session timeout. */
    private final DateTimeProperties dateTimeProperties;

    /**
     * Instantiates a new DateTimeDemoRunner.
     *
     * @param properties the date time properties
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DateTimeDemoRunner(final DateTimeProperties properties) {
        this.dateTimeProperties = properties;
    }

    @Override
    public void run(final String... args) {
        System.out.println("--- DateTimeProperties Demo ---");
        System.out.println("Launch Date: "
                + dateTimeProperties.getLaunchDate());
        System.out.println("Launch Time: "
                + dateTimeProperties.getLaunchTime());
        System.out.println("Launch Timestamp: "
                + dateTimeProperties.getLaunchTimestamp());
        System.out.println("Session Timeout (Duration): "
                + dateTimeProperties.getSessionTimeout());
    }
}
