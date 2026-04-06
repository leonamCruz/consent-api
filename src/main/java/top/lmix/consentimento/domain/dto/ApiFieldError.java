package top.lmix.consentimento.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error for a specific field")
public record ApiFieldError(
        @Schema(example = "cpf")
        String field,

        @Schema(example = "Invalid CPF")
        String message
) {
}
