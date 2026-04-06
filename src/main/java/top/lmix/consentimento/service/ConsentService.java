package top.lmix.consentimento.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentAuditEntity;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.domain.entity.ConsentSnapshot;
import top.lmix.consentimento.domain.enums.ConsentAuditAction;
import top.lmix.consentimento.domain.repository.ConsentAuditRepository;
import top.lmix.consentimento.domain.repository.ConsentRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ConsentAuditRepository consentAuditRepository;

    public ConsentCreationResult create(ConsentRequest request) {
        ConsentEntity existingConsent = consentRepository.findByCpf(request.cpf()).orElse(null);
        if (existingConsent != null) {
            if (matchesRequest(existingConsent, request)) {
                return new ConsentCreationResult(existingConsent, false);
            }
            throw duplicateCpfConflict(request.cpf());
        }

        ConsentEntity entity = new ConsentEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreationDateTime(LocalDateTime.now());
        applyChanges(entity, request);

        try {
            return new ConsentCreationResult(consentRepository.save(entity), true);
        } catch (DuplicateKeyException exception) {
            ConsentEntity persistedConsent = consentRepository.findByCpf(request.cpf()).orElse(null);
            if (persistedConsent != null && matchesRequest(persistedConsent, request)) {
                return new ConsentCreationResult(persistedConsent, false);
            }
            throw duplicateCpfConflict(request.cpf(), exception);
        }
    }

    public Page<ConsentEntity> listAll(Pageable pageable) {
        return consentRepository.findAll(pageable);
    }

    public ConsentEntity findById(UUID id) {
        return consentRepository.findById(id)
                .orElseThrow(() -> notFound(id));
    }

    public ConsentEntity update(UUID id, ConsentRequest request) {
        ConsentEntity entity = findById(id);
        assertCpfIsAvailable(request.cpf(), id);
        ConsentSnapshot beforeState = snapshotOf(entity);
        applyChanges(entity, request);
        ConsentEntity updatedEntity;
        try {
            updatedEntity = consentRepository.save(entity);
        } catch (DuplicateKeyException exception) {
            throw duplicateCpfConflict(request.cpf(), exception);
        }
        registerAudit(id, ConsentAuditAction.UPDATED, beforeState, snapshotOf(updatedEntity));

        return updatedEntity;
    }

    public void delete(UUID id) {
        ConsentEntity entity = findById(id);
        ConsentSnapshot beforeState = snapshotOf(entity);
        consentRepository.delete(entity);
        registerAudit(id, ConsentAuditAction.DELETED, beforeState, null);
    }

    private void applyChanges(ConsentEntity entity, ConsentRequest request) {
        entity.setCpf(request.cpf());
        entity.setStatus(request.status());
        entity.setAdditionalInfo(request.additionalInfo());
    }

    private boolean matchesRequest(ConsentEntity entity, ConsentRequest request) {
        return Objects.equals(entity.getCpf(), request.cpf())
                && entity.getStatus() == request.status()
                && Objects.equals(entity.getAdditionalInfo(), request.additionalInfo());
    }

    private void assertCpfIsAvailable(String cpf, UUID currentConsentId) {
        consentRepository.findByCpf(cpf)
                .filter(existing -> !existing.getId().equals(currentConsentId))
                .ifPresent(existing -> {
                    throw duplicateCpfConflict(cpf);
                });
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Consent not found for id " + id);
    }

    private ResponseStatusException duplicateCpfConflict(String cpf) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A consent already exists for cpf " + cpf + ". Reuse the existing consent or update it explicitly."
        );
    }

    private ResponseStatusException duplicateCpfConflict(String cpf, DuplicateKeyException exception) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A consent already exists for cpf " + cpf + ". Reuse the existing consent or update it explicitly.",
                exception
        );
    }

    private void registerAudit(
            UUID consentId,
            ConsentAuditAction action,
            ConsentSnapshot beforeState,
            ConsentSnapshot afterState
    ) {
        ConsentAuditEntity audit = new ConsentAuditEntity();
        audit.setId(UUID.randomUUID());
        audit.setConsentId(consentId);
        audit.setAction(action);
        audit.setChangedAt(LocalDateTime.now());
        audit.setBeforeState(beforeState);
        audit.setAfterState(afterState);
        consentAuditRepository.save(audit);
    }

    private ConsentSnapshot snapshotOf(ConsentEntity entity) {
        return new ConsentSnapshot(
                entity.getCpf(),
                entity.getStatus(),
                entity.getCreationDateTime(),
                entity.getAdditionalInfo()
        );
    }
}
