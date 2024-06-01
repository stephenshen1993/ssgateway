package com.stephenshen.ssgateway;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * hello handler.
 *
 * @author stephenshen
 * @date 2024/6/1 17:31:38
 */
@Component
public class HelloHandler {

    Mono<ServerResponse> handle(ServerRequest request) {


        String url = "http://localhost:8081/ssrpc";
        String requestJson = """
                {
                  "service": "com.stephenshen.ssrpc.demo.api.UserService",
                  "methodSign": "findById@1_int",
                  "args": [100]
                }
                """;
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJson)
                .retrieve().toEntity(String.class);

        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("response:" + source));

        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("ss.gw.version", "v1.0.0")
                .body(body, String.class);
    }

}
