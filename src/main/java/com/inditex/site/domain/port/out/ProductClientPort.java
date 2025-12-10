package com.inditex.site.domain.port.out;

import com.inditex.site.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClientPort {
    Flux<String> getSimilarIds(String productId);
    Mono<Product> getProductById(String productId);
}
