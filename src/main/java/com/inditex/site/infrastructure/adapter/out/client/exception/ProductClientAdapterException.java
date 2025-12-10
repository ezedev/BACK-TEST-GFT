package com.inditex.site.infrastructure.adapter.out.client.exception;

public class ProductClientAdapterException extends RuntimeException {
    public ProductClientAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductClientAdapterException(String message) {
        super(message);
    }
}
