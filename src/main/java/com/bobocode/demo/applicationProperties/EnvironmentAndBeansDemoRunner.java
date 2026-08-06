package com.bobocode.demo.applicationProperties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Runner that prints all beans in application context
 * and environment properties.
 */
@Component
public final class EnvironmentAndBeansDemoRunner implements CommandLineRunner {

    /** Spring application context. */
    private final ApplicationContext applicationContext;

    /** Spring environment. */
    private final Environment environment;

    /**
     * Instantiates a new EnvironmentAndBeansDemoRunner.
     *
     * @param ctx the application context
     * @param env the environment
     */
    public EnvironmentAndBeansDemoRunner(
            final ApplicationContext ctx,
            final Environment env) {
        this.applicationContext = ctx;
        this.environment = env;
    }

    @Override
    public void run(final String... args) {
        System.out.println(
                "\n================ ALL BEANS "
                        + "IN APPLICATION CONTEXT ================"
        );
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println("Bean name: " + beanName);
        }
        System.out.println("Total beans: " + beanNames.length);

        System.out.println(
                "\n================ ALL PROPERTIES "
                        + "IN ENVIRONMENT ================"
        );
        if (environment instanceof AbstractEnvironment abstractEnv) {
            for (PropertySource<?> propertySource
                    : abstractEnv.getPropertySources()) {
                if (propertySource
                        instanceof EnumerablePropertySource<?> enumerable
                ) {
                    System.out.println("--- PropertySource: "
                            + propertySource.getName() + " ---");
                    for (String propertyName : enumerable.getPropertyNames()) {
                        System.out.println("  " + propertyName + " = "
                                + environment.getProperty(propertyName));
                    }
                }
            }
        }
    }
}
