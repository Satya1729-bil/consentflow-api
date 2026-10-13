package com.satya.consentflow.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_events", indexes = @Index(name = "idx_audit_workflow_time", columnList = "workflow_id, occurred_at"))
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 180)
    private String actor;

    @Column(length = 1000)
    private String details;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt = Instant.now();

    protected AuditEvent() {}

    public AuditEvent(Long workflowId, String eventType, String actor, String details) {
        this.workflowId = workflowId;
        this.eventType = eventType;
        this.actor = actor;
        this.details = details;
    }

    public Long getId() { return id; }
    public Long getWorkflowId() { return workflowId; }
    public String getEventType() { return eventType; }
    public String getActor() { return actor; }
    public String getDetails() { return details; }
    public Instant getOccurredAt() { return occurredAt; }
}