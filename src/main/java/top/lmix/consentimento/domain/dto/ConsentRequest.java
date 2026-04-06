package top.lmix.consentimento.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;
import top.lmix.consentimento.domain.enums.Status;

public record ConsentRequest(
        @NotBlank(message = "A CPF (Brazilian taxpayer ID) is required.")
        @CPF(message = "Invalid CPF")
        @Size(min = 11, max = 11, message = "The CPF (Brazilian taxpayer ID) should only contain 11 digits.")
        String cpf,

        @NotNull(message = "Status is required")
        Status status,

        @Size(min = 1, max = 50, message = "Additional info must contain between 1 and 50 characters.")
        String additionalInfo
) {}
