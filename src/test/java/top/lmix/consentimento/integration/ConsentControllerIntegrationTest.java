package top.lmix.consentimento.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import top.lmix.consentimento.domain.dto.ConsentRequest;
import top.lmix.consentimento.domain.entity.ConsentAuditEntity;
import top.lmix.consentimento.domain.entity.ConsentEntity;
import top.lmix.consentimento.domain.enums.ConsentAuditAction;
import top.lmix.consentimento.domain.enums.Status;
import top.lmix.consentimento.domain.repository.ConsentAuditRepository;
import top.lmix.consentimento.domain.repository.ConsentRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ConsentControllerIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.uuid-representation", () -> "standard");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private ConsentAuditRepository consentAuditRepository;

    @BeforeEach
    void setUp() {
        consentRepository.deleteAll();
        consentAuditRepository.deleteAll();
    }

    @Test
    void shouldExecuteConsentLifecycleAgainstMongoDb() throws Exception {
        ConsentRequest createRequest = new ConsentRequest("12345678909", Status.ACTIVE, "first");

        String responseBody = mockMvc.perform(post("/consents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/consents/.*")))
                .andExpect(jsonPath("$.cpf").value("12345678909"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.additionalInfo").value("first"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ConsentEntity created = objectMapper.readValue(responseBody, ConsentEntity.class);

        mockMvc.perform(get("/consents/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.cpf").value("12345678909"));

        ConsentRequest updateRequest = new ConsentRequest("98765432100", Status.REVOKED, "updated");

        mockMvc.perform(put("/consents/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.status").value("REVOKED"))
                .andExpect(jsonPath("$.additionalInfo").value("updated"));

        mockMvc.perform(delete("/consents/{id}", created.getId()))
                .andExpect(status().isNoContent());

        List<ConsentAuditEntity> audits = consentAuditRepository.findByConsentIdOrderByChangedAtAsc(created.getId());
        assertEquals(2, audits.size());
        assertEquals(ConsentAuditAction.UPDATED, audits.get(0).getAction());
        assertEquals("12345678909", audits.get(0).getBeforeState().getCpf());
        assertEquals("98765432100", audits.get(0).getAfterState().getCpf());
        assertEquals(ConsentAuditAction.DELETED, audits.get(1).getAction());
        assertEquals("98765432100", audits.get(1).getBeforeState().getCpf());
        assertNull(audits.get(1).getAfterState());

        mockMvc.perform(get("/consents/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPaginatedConsents() throws Exception {
        consentRepository.save(consent("11111111111", Status.ACTIVE, "one", LocalDateTime.now().minusHours(3)));
        consentRepository.save(consent("22222222222", Status.REVOKED, "two", LocalDateTime.now().minusHours(2)));
        consentRepository.save(consent("33333333333", Status.EXPIRED, "three", LocalDateTime.now().minusHours(1)));

        mockMvc.perform(get("/consents")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "creationDateTime,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].cpf").value("33333333333"))
                .andExpect(jsonPath("$.content[1].cpf").value("22222222222"));
    }

    private ConsentEntity consent(String cpf, Status status, String additionalInfo, LocalDateTime creationDateTime) {
        ConsentEntity entity = new ConsentEntity();
        entity.setId(UUID.randomUUID());
        entity.setCpf(cpf);
        entity.setStatus(status);
        entity.setAdditionalInfo(additionalInfo);
        entity.setCreationDateTime(creationDateTime);
        return entity;
    }
}
