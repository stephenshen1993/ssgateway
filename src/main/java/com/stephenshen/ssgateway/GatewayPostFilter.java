package com.stephenshen.ssgateway;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * gateway post filter.
 * @author stephenshen
 * @date 2024/6/2 10:56:59
 */
@Component
public class GatewayPostFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).doFinally(s -> {
            System.out.println("post filter");
            exchange.getAttributes().forEach((k, v) -> System.out.println(k + ":" + v));
        });
    }
}
