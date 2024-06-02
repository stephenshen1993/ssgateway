package com.stephenshen.ssgateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway filter.
 *
 * @author stephenshen
 * @date 2024/6/2 13:04:21
 */
public interface GatewayFilter {

    Mono<Void> handle(ServerWebExchange exchange);
}
