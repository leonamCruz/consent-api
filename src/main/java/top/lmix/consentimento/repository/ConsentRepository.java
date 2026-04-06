package top.lmix.consentimento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.lmix.consentimento.domain.entity.ConsentEntity;

import java.util.UUID;

@Repository
public interface ConsentRepository extends JpaRepository<ConsentEntity, UUID> {
}
