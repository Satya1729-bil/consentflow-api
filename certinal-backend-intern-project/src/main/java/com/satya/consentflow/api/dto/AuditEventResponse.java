package com.satya.consentflow.api.dto;

import com.satya.consentflow.domain.AuditEvent;
import java.time.Instant;

public record AuditEventResponse(Long id, Long workflowId, String eventType, String actor,
                                 String details, Instant occurredAt) {
    public static AuditEventResponse from(AuditEvent e) {
        return new AuditEventResponse(e.getId(), e.getWorkflowId(), e.getEventType(), e.getActor(),
                                      e.getDetails(), e.getOccurredAt());
    }
}