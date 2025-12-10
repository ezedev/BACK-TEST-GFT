package com.inditex.site.infrastructure.adapter.out.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.inditex.site.domain.model.Product;
import com.inditex.site.domain.port.out.ProductClientPort;
import com.inditex.site.infrastructure.adapter.out.client.dto.ProductExternalDTO;
import com.inditex.site.infrastructure.adapter.out.client.exception.ProductClientAdapterException;
import com.inditex.site.infrastructure.adapter.out.client.mapper.ProductMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClientAdapter implements ProductClientPort {

    private final WebClient productWebClient;

    private final ProductMapper productMapper;

    // Cache con TTL de 5 minutos
    private final Cache<String, Flux<String>> similarIdsCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Cache<String, Mono<Product>> productByIdCache =
            Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build();

    @Override
    @CircuitBreaker(name = "productClient", fallbackMethod = "fallbackSimilarIds")
    @Retry(name = "productClient")
    public Flux<String> getSimilarIds(String productId) {

        return similarIdsCache.get(productId, id -> {
            log.info("[Cache MISS] Generating Flux for similarIds, productId: {}", id);

            return productWebClient.get()
                    .uri("/product/{id}/similarids", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ProductClientAdapterException(
                                    "4xx error calling similarIds for productId " + id
                            ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ProductClientAdapterException(
                                    "5xx error calling similarIds for productId " + id
                            ))
                    )
                    .bodyToFlux(Object.class)
                    .map(Object::toString)
                    .doOnSubscribe(sub -> log.info("[WebClient] Subscribed to similarIds, productId: {}", id)


                    )
                    .doOnNext(v ->
                            log.info("\"[WebClient] similarIds value: \": {}", v)
                    )
                    .cache();
        });
    }

    @Override
    @CircuitBreaker(name = "productClient", fallbackMethod = "fallbackProductById")
    @Retry(name = "productClient")
    public Mono<Product> getProductById(String productId) {

        return productByIdCache.get(productId, id -> {

            log.info("[Cache MISS] Generating Mono for productById, productId: {}", id);

            return productWebClient.get()
                    .uri("/product/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ProductClientAdapterException(
                                    "4xx error calling product service for productId " + id
                            ))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ProductClientAdapterException(
                                    "5xx error calling product service for productId " + id
                            ))
                    )
                    .bodyToMono(ProductExternalDTO.class)
                    .map(productMapper::toDomain)
                    .doOnSubscribe(sub ->
                            log.info("[WebClient] Subscribed to productById for productId: {}", id)
                    )
                    .doOnNext(p ->
                            log.info("[WebClient] productById received for productId: {}", id)
                    )
                    .cache();
        });
    }

    private Flux<String> fallbackSimilarIds(String productId, Throwable ex) {
        log.info("[CB FALLBACK] similarIds for productId: {} | cause: {}",
                productId,
                ex.getClass().getSimpleName());


        return Flux.error(new ProductClientAdapterException(
                "Fallback triggered for similarIds, productId " + productId, ex
        ));
    }

    private Mono<Product> fallbackProductById(String productId, Throwable ex) {
        log.info("[CB FALLBACK] productById for productId: {} | cause: {}",
                productId,
                ex.getClass().getSimpleName());


        return Mono.error(new ProductClientAdapterException(
                "Fallback triggered for productById, productId: " + productId, ex
        ));
    }
}
