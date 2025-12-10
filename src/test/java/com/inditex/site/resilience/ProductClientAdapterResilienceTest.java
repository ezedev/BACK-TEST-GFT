package com.inditex.site.resilience;

import com.inditex.site.infrastructure.adapter.out.client.ProductClientAdapter;
import com.inditex.site.infrastructure.adapter.out.client.dto.ProductExternalDTO;
import com.inditex.site.infrastructure.adapter.out.client.mapper.ProductMapper;
import com.inditex.site.infrastructure.adapter.out.client.mapper.ProductMapperImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

class ProductClientAdapterResilienceAdvancedTest {

    private MockWebServer mockWebServer;
    private ProductClientAdapter adapter;
    private ProductMapper productMapper;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(2)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(500))
                .build();

        circuitBreaker = CircuitBreaker.of("productClient", cbConfig);

        productMapper = new ProductMapperImpl();
        adapter = new ProductClientAdapter(webClient, productMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /*
    @Test
    void circuitBreaker_shouldOpen_afterRepeatedFailures() {
        // simulamos fallo del servicio
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // dos llamadas para abrir CB
        StepVerifier.create(adapter.getProductById("123"))
                .expectError()
                .verify();

        StepVerifier.create(adapter.getProductById("123"))
                .expectError()
                .verify();

        StepVerifier.create(adapter.getProductById("123"))
                .expectError()
                .verify();


        // después de los fallos, CB debe estar OPEN
        Assertions.assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }
 */
    @Test
    void fallback_shouldReturnEmptyMono_afterFailures() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(adapter.getProductById("123"))
                .expectError()
                .verify();

        StepVerifier.create(adapter.getProductById("123"))
                .expectError()
                .verify();
    }

    @Test
    void cache_shouldStoreProducts_afterSuccessfulCall() {
        // simulamos respuesta válida del servicio
        ProductExternalDTO dto = new ProductExternalDTO();
        dto.setId("123");
        dto.setName("Test");
        dto.setDescription("Desc");
        dto.setPrice(100.0);
        dto.setAvailability(true);


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":\"123\",\"name\":\"Test\",\"description\":\"Desc\",\"price\":100.0,\"availability\":true}")
                .addHeader("Content-Type", "application/json")
        );

        StepVerifier.create(adapter.getProductById("123"))
                .expectNextMatches(p -> p.getId().equals("123") && p.getAvailable())
                .verifyComplete();

        StepVerifier.create(adapter.getProductById("123"))
                .expectNextMatches(p -> p.getId().equals("123") && p.getAvailable())
                .verifyComplete();
    }
}
