package com.bobocode.demo.applicationProperties;

import com.bobocode.configs.DateTimeProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DateTimeDemoRunner implements CommandLineRunner {

    private final DateTimeProperties dateTimeProperties;

    public DateTimeDemoRunner(DateTimeProperties dateTimeProperties) {
        this.dateTimeProperties = dateTimeProperties;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- DateTimeProperties Demo ---");
        System.out.println("Launch Date: " + dateTimeProperties.getLaunchDate());
        System.out.println("Launch Time: " + dateTimeProperties.getLaunchTime());
        System.out.println("Launch Timestamp: " + dateTimeProperties.getLaunchTimestamp());
        System.out.println("Session Timeout (Duration): " + dateTimeProperties.getSessionTimeout());
    }
}