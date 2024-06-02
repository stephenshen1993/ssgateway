package com.stephenshen.ssgateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway plugin.
 *
 * @author stephenshen
 * @date 2024/6/2 11:12:40
 */
public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    void start();
    void stop();

    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
