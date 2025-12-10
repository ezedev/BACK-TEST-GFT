package com.inditex.site.application.exception;

public class SimilarProductsUnavailableException extends RuntimeException {
    public SimilarProductsUnavailableException(String productId) {
        super("Similar products unavailable for product: " + productId);
    }
}
