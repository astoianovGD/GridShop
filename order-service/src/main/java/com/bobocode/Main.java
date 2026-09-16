package com.bobocode;

import com.bobocode.configs.CustomLoadBalancerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Main application class.
 */
@EnableFeignClients
@SpringBootApplication
@LoadBalancerClients(value = {
        @LoadBalancerClient(name = "user-service", configuration = CustomLoadBalancerConfiguration.class),
        @LoadBalancerClient(name = "product-service", configuration = CustomLoadBalancerConfiguration.class)
})
public class Main {

    /**
     * Protected constructor to hide
     * the implicit public one for utility/main classes
     * while allowing Spring/CGLIB proxying.
     */
    protected Main() {
    }

    /**
     * Main entry point for the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone
                .getTimeZone("Europe/Kyiv")
        );
        SpringApplication.run(Main.class, args);
    }
}
