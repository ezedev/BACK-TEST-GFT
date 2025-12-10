package com.inditex.site.domain.port.in;

import com.inditex.site.domain.model.Product;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface GetSimilarProductsUseCase {

    Flux<Product> execute(String productId);
}
