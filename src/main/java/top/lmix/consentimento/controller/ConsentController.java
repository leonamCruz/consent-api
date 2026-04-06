package top.lmix.consentimento.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import top.lmix.consentimento.domain.dto.ApiErrorResponse;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.service.ConsentService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/consents")
@RequiredArgsConstructor
@Tag(name = "Consents", description = "Operacoes de gerenciamento de consentimentos")
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    @Operation(summary = "Criar um novo consentimento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consentimento criado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ConsentEntity> create(@RequestBody @Valid ConsentRequest entity) {
        ConsentEntity savedConsent = consentService.create(entity);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedConsent.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedConsent);
    }

    @GetMapping
    @Operation(summary = "Listar consentimentos com paginacao")
    @ApiResponse(responseCode = "200", description = "Pagina de consentimentos")
    public Page<ConsentEntity> listAll(
            @PageableDefault(size = 10, sort = "creationDateTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return consentService.listAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consentimento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consentimento encontrado"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consentimento nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ConsentEntity> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(consentService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consentimento e registrar rastreabilidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consentimento atualizado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consentimento nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<ConsentEntity> update(@PathVariable UUID id, @RequestBody @Valid ConsentRequest details) {
        return ResponseEntity.ok(consentService.update(id, details));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir consentimento e registrar rastreabilidade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consentimento excluido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consentimento nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        consentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
