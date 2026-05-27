package com.workspace.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. Generate unique ID cho request này
        String requestId = UUID.randomUUID().toString();
        Instant start = Instant.now();

        ServerHttpRequest request = exchange.getRequest();

        // 2. Log incoming request
        log.info("→ [{}] {} {} from {}",
                requestId,
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress());

        // 3. Inject X-Request-Id vào request gửi xuống downstream service
        //    (auth-service sẽ thấy header này, có thể log cùng để correlate)
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        // 4. Inject X-Request-Id vào response trả về client
        exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, requestId);

        // 5. Forward request qua filter chain, sau đó log response
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signalType -> {
                    long durationMs = Duration.between(start, Instant.now()).toMillis();
                    log.info("← [{}] {} in {}ms",
                            requestId,
                            exchange.getResponse().getStatusCode(),
                            durationMs);
                });
    }

    @Override
    public int getOrder() {
        // Số càng nhỏ → chạy càng sớm. -1 đảm bảo filter này chạy TRƯỚC route logic
        return Ordered.HIGHEST_PRECEDENCE;
    }
}