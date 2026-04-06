package top.lmix.consentimento.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.domain.repository.ConsentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    public ConsentEntity create(ConsentRequest request) {
        ConsentEntity entity = new ConsentEntity();
        entity.setId(UUID.randomUUID());
        entity.setCreationDateTime(LocalDateTime.now());
        applyChanges(entity, request);

        return consentRepository.save(entity);
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
        applyChanges(entity, request);

        return consentRepository.save(entity);
    }

    public void delete(UUID id) {
        ConsentEntity entity = findById(id);
        consentRepository.delete(entity);
    }

    private void applyChanges(ConsentEntity entity, ConsentRequest request) {
        entity.setCpf(request.cpf());
        entity.setStatus(request.status());
        entity.setAdditionalInfo(request.additionalInfo());
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Consent not found for id " + id);
    }
}
