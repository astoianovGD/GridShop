package com.bobocode.demo.applicationProperties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentAndBeansDemoRunner implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    private final Environment environment;

    public EnvironmentAndBeansDemoRunner(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n================ ALL BEANS IN APPLICATION CONTEXT ================");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println("Bean name: " + beanName);
        }
        System.out.println("Total beans: " + beanNames.length);

        System.out.println("\n================ ALL PROPERTIES IN ENVIRONMENT ================");
        if (environment instanceof AbstractEnvironment abstractEnv) {
            for (PropertySource<?> propertySource : abstractEnv.getPropertySources()) {
                if (propertySource instanceof EnumerablePropertySource<?> enumerable) {
                    System.out.println("--- PropertySource: " + propertySource.getName() + " ---");
                    for (String propertyName : enumerable.getPropertyNames()) {
                        // Щоб не виводити чутливі дані повністю, можна замаскувати паролі, але для завдання виведемо все
                        System.out.println("  " + propertyName + " = " + environment.getProperty(propertyName));
                    }
                }
            }
        }
    }
}