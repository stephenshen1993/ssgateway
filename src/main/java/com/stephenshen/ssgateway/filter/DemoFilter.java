package com.stephenshen.ssgateway.filter;

import com.stephenshen.ssgateway.GatewayFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * demo filter.
 *
 * @author stephenshen
 * @date 2024/6/2 13:07:25
 */
@Component("demoFilter")
public class DemoFilter implements GatewayFilter {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println(" ===>>> filter: demo filter ...");
        exchange.getRequest().getHeaders()
                .forEach((k, v) -> System.out.println(k + ":" + v));
        return Mono.empty();
    }
}
