package com.inditex.site.infrastructure.adapter.in.rest.controller;

import com.inditex.site.domain.model.Product;
import com.inditex.site.domain.port.in.GetSimilarProductsUseCase;
import com.inditex.site.infrastructure.adapter.in.rest.dto.ProductDTO;
import com.inditex.site.infrastructure.adapter.in.rest.mapper.ProductDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerTest {

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private SimilarProductsController controller;

    @Mock
    private ServerWebExchange exchange;

    @Test
    void should_return_similar_products() {
        Product product1 = Product.builder()
                .id("1")
                .name("Zapatilla")
                .description("Running")
                .price(100.0)
                .available(true)
                .build();

        Product product2 = Product.builder()
                .id("2")
                .name("Botín")
                .description("Fútbol")
                .price(120.0)
                .available(false)
                .build();

        ProductDTO dto1 = new ProductDTO();
        dto1.setId("1");
        dto1.setDescription("Zapatilla");
        dto1.setName("Running");
        dto1.setPrice(100.0F);
        dto1.setAvailability(true);

        ProductDTO dto2 = new ProductDTO();
        dto2.setId("2");
        dto2.setDescription("Botin");
        dto2.setName("Futbol");
        dto2.setPrice(120.0F);
        dto2.setAvailability(true);

        when(getSimilarProductsUseCase.execute("123"))
                .thenReturn(Flux.just(product1, product2));

        when(productDtoMapper.toDto(product1)).thenReturn(dto1);
        when(productDtoMapper.toDto(product2)).thenReturn(dto2);

        Mono<ResponseEntity<Flux<ProductDTO>>> result =
                controller.productIdSimilarGet("123", exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    StepVerifier.create(response.getBody())
                            .expectNext(dto1)
                            .expectNext(dto2)
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void should_return_empty_flux_when_use_case_fails() {
        when(getSimilarProductsUseCase.execute("999"))
                .thenReturn(Flux.error(new RuntimeException("Boom")));

        Mono<ResponseEntity<Flux<ProductDTO>>> result =
                controller.productIdSimilarGet("999", exchange);

        StepVerifier.create(result)
                .assertNext(response -> {
                    StepVerifier.create(response.getBody())
                            .verifyComplete();
                })
                .verifyComplete();
    }
}
