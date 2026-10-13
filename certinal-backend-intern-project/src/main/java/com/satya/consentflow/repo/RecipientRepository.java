package com.satya.consentflow.repo;

import com.satya.consentflow.domain.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByIdAndWorkflowId(Long id, Long workflowId);
}