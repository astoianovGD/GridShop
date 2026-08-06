package com.bobocode.demo.DirtyContext;

import org.springframework.stereotype.Service;

/**
 * Service class that maintains a simple counter.
 */
@Service
public final class CounterService {

    /** Current counter value. */
    private int counter = 0;

    /**
     * Increments the counter value by 1.
     */
    public void increment() {
        counter++;
    }

    /**
     * Retrieves the current counter value.
     *
     * @return current counter
     */
    public int getCounter() {
        return counter;
    }
}
