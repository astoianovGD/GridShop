package com.bobocode.demo.applicationProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Demo component that parses features from properties using SpEL.
 */
@Component
public final class SpelPropertiesDemo {

    /** Array of loaded features. */
    @Value("#{'${app.features}'.split('-')}")
    private String[] featuresArray;

    /**
     * Prints loaded features to standard output.
     */
    @PostConstruct
    public void printFeatures() {
        System.out.println("Features loaded via SpEL: "
                + Arrays.toString(featuresArray));
    }
}
