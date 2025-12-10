package com.inditex.site.application.usecase;

import com.inditex.site.application.exception.SimilarProductsUnavailableException;
import com.inditex.site.domain.exception.ProductNotFoundException;
import com.inditex.site.domain.model.Product;
import com.inditex.site.domain.port.in.GetSimilarProductsUseCase;
import com.inditex.site.domain.port.out.ProductClientPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetSimilarProductsService implements GetSimilarProductsUseCase {

    private final ProductClientPort productClientPort;

    public GetSimilarProductsService(ProductClientPort productClientPort) {
        this.productClientPort = productClientPort;
    }

    @Override
    public Flux<Product> execute(String productId) {

        return productClientPort.getSimilarIds(productId)

                .switchIfEmpty(Flux.error(
                        new SimilarProductsUnavailableException(productId)
                ))

                .concatMap(id ->
                        productClientPort.getProductById(id)
                                .switchIfEmpty(Mono.error(
                                        new ProductNotFoundException(id)
                                ))
                );
    }

}
