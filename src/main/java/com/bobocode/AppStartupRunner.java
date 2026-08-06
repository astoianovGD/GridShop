package com.bobocode;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Runner that starts the console session manager when the application boots.
 * Disabled during tests.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
public class AppStartupRunner implements CommandLineRunner {

    /** Session manager for handling console interaction loops. */
    private final ConsoleSessionManager sessionManager;

    /**
     * Callback used to run the application.
     *
     * @param args incoming command line arguments
     */
    @Override
    public final void run(final String... args) {
        //System.out.println(">>> AppStartupRunner started"); for debugging
        sessionManager.startSession();
    }
}
