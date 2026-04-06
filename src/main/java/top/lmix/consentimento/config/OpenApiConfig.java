package top.lmix.consentimento.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Consentimento API",
                version = "v1",
                description = "API REST para gerenciamento de consentimentos e rastreabilidade de alterações.",
                contact = @Contact(name = "LMix"),
                license = @License(name = "Apache 2.0")
        )
)
public class OpenApiConfig {
}
