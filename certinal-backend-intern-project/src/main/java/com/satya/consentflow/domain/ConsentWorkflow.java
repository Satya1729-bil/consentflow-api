package com.satya.consentflow.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consent_workflows")
public class ConsentWorkflow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(name = "created_by", nullable = false, length = 180)
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkflowStatus status = WorkflowStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Recipient> recipients = new ArrayList<>();

    protected ConsentWorkflow() {}

    public ConsentWorkflow(String title, String createdBy) {
        this.title = title;
        this.createdBy = createdBy;
    }

    @PreUpdate void touch() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCreatedBy() { return createdBy; }
    public WorkflowStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<Recipient> getRecipients() { return recipients; }
    public void setStatus(WorkflowStatus status) { this.status = status; }
    public void addRecipient(Recipient recipient) { recipients.add(recipient); recipient.setWorkflow(this); }
}