package com.bobocode.concurrency.letters;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom thread-safe data structure specialized for counting English letters (a-z).
 * Uses fine-grained locking (lock striping) with 26 independent monitor objects,
 * one per letter, so threads updating different letters never block each other.
 */
public class LetterCountTable {

    private static final int ALPHABET_SIZE = 26;

    private final long[] counts = new long[ALPHABET_SIZE];
    private final Object[] locks = new Object[ALPHABET_SIZE];

    public LetterCountTable() {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            locks[i] = new Object();
        }
    }

    /**
     * Increments the counter for the given lowercase ASCII letter ('a'..'z').
     *
     * @param letter character to count
     */
    public void increment(final char letter) {
        char lower = Character.toLowerCase(letter);
        if (lower < 'a' || lower > 'z') {
            return;
        }
        int index = lower - 'a';
        synchronized (locks[index]) {
            counts[index]++;
        }
    }

    /**
     * Adds a delta count for the given lowercase ASCII letter ('a'..'z').
     *
     * @param letter character to count
     * @param delta  amount to add
     */
    public void add(final char letter, final long delta) {
        char lower = Character.toLowerCase(letter);
        if (lower < 'a' || lower > 'z') {
            return;
        }
        int index = lower - 'a';
        synchronized (locks[index]) {
            counts[index] += delta;
        }
    }

    /**
     * Returns a snapshot of all non-zero letter counts as a standard Map.
     *
     * @return map of letter to count
     */
    public Map<Character, Long> toMap() {
        Map<Character, Long> snapshot = new LinkedHashMap<>();
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            long value;
            synchronized (locks[i]) {
                value = counts[i];
            }
            if (value > 0) {
                snapshot.put((char) ('a' + i), value);
            }
        }
        return snapshot;
    }
}
