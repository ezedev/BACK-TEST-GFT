package com.inditex.site.infrastructure.adapter.in.rest.controller;

import com.inditex.site.domain.port.in.GetSimilarProductsUseCase;
import com.inditex.site.infrastructure.adapter.in.rest.api.DefaultApi;
import com.inditex.site.infrastructure.adapter.in.rest.dto.ProductDTO;
import com.inditex.site.infrastructure.adapter.in.rest.mapper.ProductDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class SimilarProductsController implements DefaultApi {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    private final ProductDtoMapper productDtoMapper;

    @Override
    public Mono<ResponseEntity<Flux<ProductDTO>>> productIdSimilarGet(
            String id,
            ServerWebExchange exchange
    ) {
        Flux<ProductDTO> body = getSimilarProductsUseCase.execute(id)
                .map(productDtoMapper::toDto)
                .onErrorResume(ex -> Flux.empty());

        return Mono.just(ResponseEntity.ok(body));
    }
}
