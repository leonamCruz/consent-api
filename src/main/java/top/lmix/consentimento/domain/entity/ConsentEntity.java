package top.lmix.consentimento.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import top.lmix.consentimento.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "consents")
@Getter
@Setter
@NoArgsConstructor
public class ConsentEntity {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String cpf;

    private Status status;

    private LocalDateTime creationDateTime;

    private String additionalInfo;
}
