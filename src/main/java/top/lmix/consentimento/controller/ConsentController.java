package top.lmix.consentimento.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.service.ConsentService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    public ResponseEntity<ConsentEntity> create(@RequestBody @Valid ConsentRequest entity) {
        ConsentEntity savedConsent = consentService.create(entity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedConsent.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedConsent);
    }

    @GetMapping
    public Page<ConsentEntity> listAll(
            @PageableDefault(size = 10, sort = "creationDateTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return consentService.listAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsentEntity> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(consentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsentEntity> update(@PathVariable UUID id, @RequestBody @Valid ConsentRequest details) {
        return ResponseEntity.ok(consentService.update(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        consentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
