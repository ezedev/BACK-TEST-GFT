package com.inditex.site.infrastructure.adapter.in.rest.error;

import com.inditex.site.application.exception.ExternalDependencyUnavailableException;
import com.inditex.site.application.exception.SimilarProductsUnavailableException;
import com.inditex.site.domain.exception.ProductNotFoundException;
import com.inditex.site.infrastructure.adapter.in.rest.error.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(
                HttpStatus.NOT_FOUND, ex.getMessage()
        ));
    }

    @ExceptionHandler(SimilarProductsUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailable(SimilarProductsUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()
        ));
    }

    @ExceptionHandler(ExternalDependencyUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleExternal(
            ExternalDependencyUnavailableException ex
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiErrorResponse(
                        "EXTERNAL_DEPENDENCY_UNAVAILABLE",
                        ex.getMessage(),
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        Instant.now()
                ));
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"
        ));
    }

    private Map<String, Object> error(HttpStatus status, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
    }
}
