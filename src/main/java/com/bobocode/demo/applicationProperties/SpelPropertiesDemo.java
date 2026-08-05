package com.bobocode.demo.applicationProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class SpelPropertiesDemo {

    @Value("#{'${app.features}'.split('-')}")
    private String[] featuresArray;

    @PostConstruct
    public void printFeatures() {
        System.out.println("Features loaded via SpEL: " + Arrays.toString(featuresArray));
    }
}