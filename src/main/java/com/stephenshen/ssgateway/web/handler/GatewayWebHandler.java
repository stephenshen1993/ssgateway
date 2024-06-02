package com.stephenshen.ssgateway.web.handler;

import com.stephenshen.ssgateway.DefaultGatewayPluginChain;
import com.stephenshen.ssgateway.GatewayPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * gateway web handler.
 *
 * @author stephenshen
 * @date 2024/6/2 07:51:23
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    private List<GatewayPlugin> plugins;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println("====> ss gateway web handler ...");

        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result": "no plugin"}
                    """;
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));

        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);

//        for (GatewayPlugin plugin : plugins) {
//            if (plugin.support(exchange)) {
//                return plugin.handle(exchange);
//            }
//        }

//        String mock = """
//                    {"result": "no supported plugin"}
//                    """;
//        return exchange.getResponse()
//                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
