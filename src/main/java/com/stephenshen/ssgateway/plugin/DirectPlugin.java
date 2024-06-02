package com.stephenshen.ssgateway.plugin;

import com.stephenshen.ssgateway.AbstractPlugin;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * direct proxy plugin.
 * @author stephenshen
 * @date 2024/6/2 11:26:30
 */
@Component("direct")
public class DirectPlugin extends AbstractPlugin {
    public static final String NAME = "direct";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println("======>>>>>> [DirectPlugin] ...");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ss.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("ss.gw.plugin", getName());

        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x))).then();
        }

        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        Mono<String> body = entity.map(ResponseEntity::getBody);

        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Flux.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))));
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
