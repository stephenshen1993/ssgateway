package com.stephenshen.ssgateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway plugin chain.
 *
 * @author stephenshen
 * @date 2024/6/2 12:42:49
 */
public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);
}
