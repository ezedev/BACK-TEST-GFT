package com.inditex.site.infrastructure.adapter.in.rest.error.dto;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        Instant timestamp
) {}
