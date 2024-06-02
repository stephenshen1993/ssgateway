package com.stephenshen.ssgateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author stephenshen
 * @date 2024/6/2 11:14:34
 */
public abstract class AbstractPlugin implements GatewayPlugin{

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean supported = doSupport(exchange);
        System.out.println(" =====>>>> plugin[" + this.getName() + "], support=" + supported);
        return supported ? doHandle(exchange, chain) : chain.handle(exchange);
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);
    public abstract boolean doSupport(ServerWebExchange exchange);
}
