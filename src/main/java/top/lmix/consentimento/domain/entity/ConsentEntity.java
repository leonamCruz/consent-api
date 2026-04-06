package top.lmix.consentimento.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import top.lmix.consentimento.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent")
@Getter
@Setter
@NoArgsConstructor
public class ConsentEntity {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(length = 11, nullable = false)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;
}