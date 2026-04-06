package top.lmix.consentimento.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Standard API error payload")
public record ApiErrorResponse(
        @Schema(example = "2026-04-06T16:30:00")
        LocalDateTime timestamp,

        @Schema(example = "400")
        int status,

        @Schema(example = "Bad Request")
        String error,

        @Schema(example = "Validation failed")
        String message,

        @Schema(example = "/consents")
        String path,

        List<ApiFieldError> fieldErrors
) {
}
