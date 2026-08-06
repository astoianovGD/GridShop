package com.bobocode.IntegrationTestsExample;

import com.bobocode.demo.DirtyContext.CounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CounterTest1 {

    @Autowired
    CounterService counterService;

    @Autowired
    private Environment environment;

    @Test
    void printProfiles() {
        System.out.println(Arrays.toString(environment.getActiveProfiles()));
    }

    @Test
    void incrementCounter() {
        counterService.increment();
        assertEquals(1, counterService.getCounter());
    }
}
