package com.stephenshen.ssgateway;

import com.stephenshen.ssrpc.core.api.LoadBalancer;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.cluster.RandomLoadBalancer;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * ggateway handler.
 *
 * @author stephenshen
 * @date 2024/6/1 17:31:38
 */
@Component
public class GatewayHandler {

    @Autowired
    private RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RandomLoadBalancer<>();

    Mono<ServerResponse> handle(ServerRequest request) {
        // 1、通过请求路径获取服务名
        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2、通过注册中心获取所有活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 3、先简化处理，获取第一个实例url
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        System.out.println(" inst size=" + instanceMetas.size() + ", inst " + instanceMeta);
        String url = instanceMeta.toUrl();

        // 4、拿到请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);

        return requestMono.flatMap(x -> {
            return invokeFromRegistry(x, url);
        });
    }

    private static @NotNull Mono<ServerResponse> invokeFromRegistry(String x, String url) {
        // 5、通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(x).retrieve().toEntity(String.class);

        // 6、通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("response:" + source));

        // 7、组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("ss.gw.version", "v1.0.0")
                .body(body, String.class);
    }

}
