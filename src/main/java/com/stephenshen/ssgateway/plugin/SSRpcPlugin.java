package com.stephenshen.ssgateway.plugin;

import com.stephenshen.ssgateway.AbstractPlugin;
import com.stephenshen.ssgateway.GatewayPluginChain;
import com.stephenshen.ssrpc.core.api.LoadBalancer;
import com.stephenshen.ssrpc.core.api.RegistryCenter;
import com.stephenshen.ssrpc.core.cluster.RandomLoadBalancer;
import com.stephenshen.ssrpc.core.meta.InstanceMeta;
import com.stephenshen.ssrpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * ss rpc gateway plugin.
 * @author stephenshen
 * @date 2024/6/2 11:21:44
 */
@Component("ssrpc")
public class SSRpcPlugin extends AbstractPlugin {

    public static final String NAME = "ssrpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Autowired
    private RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RandomLoadBalancer<>();

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        System.out.println("======>>>>>> [SSRpcPlugin] ...");

        // 1、通过请求路径获取服务名
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2、通过注册中心获取所有活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 3、先简化处理，获取第一个实例url
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        System.out.println(" inst size=" + instanceMetas.size() + ", inst " + instanceMeta);
        String url = instanceMeta.toUrl();

        // 4、拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5、通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);

        // 6、通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        // body.subscribe(source -> System.out.println("response:" + source));

        // 7、组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ss.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("ss.gw.plugin", getName());
        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Flux.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));
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
