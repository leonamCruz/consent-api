package top.lmix.consentimento.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import top.lmix.consentimento.domain.entity.ConsentAuditEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConsentAuditRepository extends MongoRepository<ConsentAuditEntity, UUID> {

    List<ConsentAuditEntity> findByConsentIdOrderByChangedAtAsc(UUID consentId);
}
