package com.satya.consentflow.repo;

import com.satya.consentflow.domain.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByWorkflowIdOrderByOccurredAtAsc(Long workflowId);
}