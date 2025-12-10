package com.inditex.site.contract;

import com.inditex.site.infrastructure.adapter.in.rest.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class SimilarProductsContractTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToApplicationContext(context)
                .build();
    }

    @Test
    void contract_shouldReturnValidProductList() {
        client.get()
                .uri("/product/{id}/similar", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .consumeWith(response -> {
                    var products = response.getResponseBody();
                    assert products != null;
                    assert !products.isEmpty();
                    products.forEach(product -> {
                        assert product.getId() != null;
                        assert product.getName() != null;
                        assert product.getAvailability() != null;
                    });
                });
    }
}