package com.inditex.site.application.usecase;

import com.inditex.site.application.exception.SimilarProductsUnavailableException;
import com.inditex.site.domain.exception.ProductNotFoundException;
import com.inditex.site.domain.model.Product;
import com.inditex.site.domain.port.out.ProductClientPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsServiceTest {

    @Mock
    private ProductClientPort productClientPort;

    @InjectMocks
    private GetSimilarProductsService service;

    @Test
    void should_return_similar_products_successfully() {
        String productId = "1";

        Product product1 = Product.builder()
                .id("2")
                .name("Zapatilla")
                .description("Running")
                .price(100.0)
                .available(true)
                .build();

        Product product2 = Product.builder()
                .id("3")
                .name("Botín")
                .description("Fútbol")
                .price(120.0)
                .available(false)
                .build();

        when(productClientPort.getSimilarIds(productId))
                .thenReturn(Flux.just("2", "3"));

        when(productClientPort.getProductById("2"))
                .thenReturn(Mono.just(product1));

        when(productClientPort.getProductById("3"))
                .thenReturn(Mono.just(product2));

        Flux<Product> result = service.execute(productId);

        StepVerifier.create(result)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();
    }

    @Test
    void should_throw_SimilarProductsUnavailableException_when_no_similar_ids() {
        // GIVEN
        String productId = "1";

        when(productClientPort.getSimilarIds(productId))
                .thenReturn(Flux.empty());

        Flux<Product> result = service.execute(productId);

        StepVerifier.create(result)
                .expectError(SimilarProductsUnavailableException.class)
                .verify();
    }

    @Test
    void should_throw_ProductNotFoundException_when_product_not_found() {
        String productId = "1";

        when(productClientPort.getSimilarIds(productId))
                .thenReturn(Flux.just("2"));

        when(productClientPort.getProductById("2"))
                .thenReturn(Mono.empty());

        Flux<Product> result = service.execute(productId);

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();
    }
}
