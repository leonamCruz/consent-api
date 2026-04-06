package top.lmix.consentimento.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import top.lmix.consentimento.domain.entity.ConsentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends MongoRepository<ConsentEntity, UUID> {

    Optional<ConsentEntity> findByCpf(String cpf);
}
