package top.lmix.consentimento.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.domain.enums.Status;
import top.lmix.consentimento.domain.repository.ConsentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @InjectMocks
    private ConsentService consentService;

    @Test
    void createShouldGenerateIdAndCreationDateTimeBeforeSaving() {
        ConsentRequest request = new ConsentRequest("12345678909", Status.ACTIVE, "extra");
        when(consentRepository.save(any(ConsentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConsentEntity saved = consentService.create(request);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreationDateTime());
        assertEquals(request.cpf(), saved.getCpf());
        assertEquals(request.status(), saved.getStatus());
        assertEquals(request.additionalInfo(), saved.getAdditionalInfo());

        ArgumentCaptor<ConsentEntity> captor = ArgumentCaptor.forClass(ConsentEntity.class);
        verify(consentRepository).save(captor.capture());
        assertNotNull(captor.getValue().getId());
        assertNotNull(captor.getValue().getCreationDateTime());
    }

    @Test
    void listAllShouldDelegateToRepository() {
        PageRequest pageable = PageRequest.of(0, 2);
        Page<ConsentEntity> expectedPage = new PageImpl<>(List.of(new ConsentEntity()));
        when(consentRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<ConsentEntity> actualPage = consentService.listAll(pageable);

        assertSame(expectedPage, actualPage);
        verify(consentRepository).findAll(pageable);
    }

    @Test
    void updateShouldApplyChangesToExistingConsent() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        ConsentEntity existing = new ConsentEntity();
        existing.setId(id);
        existing.setCreationDateTime(createdAt);
        existing.setCpf("12345678909");
        existing.setStatus(Status.ACTIVE);
        existing.setAdditionalInfo("old");

        ConsentRequest request = new ConsentRequest("98765432100", Status.REVOKED, "new");

        when(consentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(consentRepository.save(existing)).thenReturn(existing);

        ConsentEntity updated = consentService.update(id, request);

        assertEquals(id, updated.getId());
        assertEquals(createdAt, updated.getCreationDateTime());
        assertEquals(request.cpf(), updated.getCpf());
        assertEquals(request.status(), updated.getStatus());
        assertEquals(request.additionalInfo(), updated.getAdditionalInfo());
        verify(consentRepository).save(existing);
    }

    @Test
    void deleteShouldRemoveExistingConsent() {
        UUID id = UUID.randomUUID();
        ConsentEntity existing = new ConsentEntity();
        existing.setId(id);
        when(consentRepository.findById(id)).thenReturn(Optional.of(existing));

        consentService.delete(id);

        verify(consentRepository).delete(existing);
    }

    @Test
    void findByIdShouldThrowNotFoundWhenConsentDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(consentRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> consentService.findById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Consent not found for id " + id, exception.getReason());
    }
}
