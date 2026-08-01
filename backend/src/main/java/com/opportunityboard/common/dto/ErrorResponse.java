package com.opportunityboard.common.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<FieldErrorItem> fieldErrors
) {
    public record FieldErrorItem(
            String field,
            String message
    ) {
    }
}
