package com.stephenshen.ssgateway;

import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.registry.ss.SsRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
