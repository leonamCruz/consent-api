package top.lmix.consentimento.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.lmix.consentimento.domain.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsentSnapshot {

    private String cpf;

    private Status status;

    private LocalDateTime creationDateTime;

    private String additionalInfo;
}
