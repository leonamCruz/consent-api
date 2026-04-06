package top.lmix.consentimento.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import top.lmix.consentimento.domain.enums.ConsentAuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "consent_audits")
@Getter
@Setter
@NoArgsConstructor
public class ConsentAuditEntity {

    @Id
    private UUID id;

    private UUID consentId;

    private ConsentAuditAction action;

    private LocalDateTime changedAt;

    private ConsentSnapshot beforeState;

    private ConsentSnapshot afterState;
}
