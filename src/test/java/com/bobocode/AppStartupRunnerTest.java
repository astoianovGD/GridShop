package com.bobocode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AppStartupRunnerTest {

    @Mock
    private ConsoleSessionManager sessionManager;

    @InjectMocks
    private AppStartupRunner appStartupRunner;

    @Test
    void shouldStartSessionSuccessfullyOnRun() {
        appStartupRunner.run();

        verify(sessionManager).startSession();
    }
}