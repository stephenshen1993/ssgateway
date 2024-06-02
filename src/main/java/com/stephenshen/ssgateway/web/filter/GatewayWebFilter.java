package com.stephenshen.ssgateway.web.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * gateway web filter.
 *
 * @author stephenshen
 * @date 2024/6/2 10:52:00
 */
@Component
public class GatewayWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("===>>> SS Gateway web filter ...");
        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }
        String mock = """
                {"result": "mock"}
                """;
        return exchange.getResponse()
                .writeWith(Flux.just(exchange.getResponse()
                        .bufferFactory().wrap(mock.getBytes())));
    }
}
