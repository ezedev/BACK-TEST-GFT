package com.inditex.site.infrastructure.adapter.out.client;

import com.inditex.site.domain.model.Product;
import com.inditex.site.infrastructure.adapter.out.client.dto.ProductExternalDTO;
import com.inditex.site.infrastructure.adapter.out.client.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductClientAdapterTest {

    @Mock
    private WebClient productWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;


    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductClientAdapter productClientAdapter;


    @BeforeEach
    void setUp() {
        when(productWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), (Object) any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
    }

    @Test
    void should_return_similar_ids_successfully() {
        String productId = "1";

        when(responseSpec.bodyToFlux(Object.class))
                .thenReturn(Flux.just(2, 3, 4));

        Flux<String> result =
                productClientAdapter.getSimilarIds(productId);

        StepVerifier.create(result)
                .expectNext("2", "3", "4")
                .verifyComplete();
    }

    @Test
    void should_return_error_when_similar_ids_service_fails() {


        String productId = "1";

        when(responseSpec.bodyToFlux(Object.class))
                .thenReturn(Flux.error(new RuntimeException("boom")));

        Flux<String> result =
                productClientAdapter.getSimilarIds(productId);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void should_return_product_successfully() {
        String productId = "2";

        ProductExternalDTO externalDTO =  new ProductExternalDTO();
        externalDTO.setId("2");
        externalDTO.setName("Zapatilla");
        externalDTO.setDescription("Running");
        externalDTO.setPrice(100.0);
        externalDTO.setAvailability(true);


        Product product = Product.builder()
                .id("2")
                .name("Zapatilla")
                .description("Running")
                .price(100.0)
                .available(true)
                .build();

        when(responseSpec.bodyToMono(ProductExternalDTO.class))
                .thenReturn(Mono.just(externalDTO));

        when(productMapper.toDomain(externalDTO))
                .thenReturn(product);

        Mono<Product> result =
                productClientAdapter.getProductById(productId);

        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void should_complete_empty_when_product_not_found() {
        String productId = "99";

        when(responseSpec.bodyToMono(ProductExternalDTO.class))
                .thenReturn(Mono.empty());

        Mono<Product> result =
                productClientAdapter.getProductById(productId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void should_fail_when_mapper_throws_error() {
        String productId = "2";

        ProductExternalDTO externalDTO = new ProductExternalDTO();
        externalDTO.setId("2");

        when(responseSpec.bodyToMono(ProductExternalDTO.class))
                .thenReturn(Mono.just(externalDTO));

        when(productMapper.toDomain(externalDTO))
                .thenThrow(new RuntimeException("mapping error"));

        Mono<Product> result =
                productClientAdapter.getProductById(productId);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
