package com.bobocode.configs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomLoadBalancerConfigurationTest {

    @Test
    @DisplayName("randomLoadBalancer bean should return RandomLoadBalancer instance")
    void shouldCreateRandomLoadBalancer() {
        CustomLoadBalancerConfiguration configuration = new CustomLoadBalancerConfiguration();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(LoadBalancerClientFactory.PROPERTY_NAME, "user-service");

        LoadBalancerClientFactory loadBalancerClientFactory =
                new LoadBalancerClientFactory(new LoadBalancerClientsProperties());

        ReactorLoadBalancer<ServiceInstance> loadBalancer =
                configuration.randomLoadBalancer(environment, loadBalancerClientFactory);

        assertNotNull(loadBalancer);
        assertInstanceOf(RandomLoadBalancer.class, loadBalancer);
    }
}
