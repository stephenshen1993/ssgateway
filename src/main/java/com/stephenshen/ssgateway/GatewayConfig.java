package com.stephenshen.ssgateway;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.registry.ss.SsRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;
import java.util.Properties;

/**
 * gateway config.
 *
 * @author stephenshen
 * @date 2024/6/1 18:25:17
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc() {
        return new SsRegistryCenter();
    }

    @Bean
    public ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "gatewayWebHandle");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("ssrpc gateway start");
        };
    }
}
