package com.bobocode.IntegrationTestsExample;

import com.bobocode.demo.DirtyContext.CounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
class CounterTest2 {

    @Autowired
    CounterService counterService;

    @Test
    void shouldStartFromZero() {
        assertEquals(0, counterService.getCounter());
    }
}
