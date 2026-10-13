package com.satya.consentflow.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workflow_recipients",
       uniqueConstraints = @UniqueConstraint(name = "uk_workflow_recipient_email", columnNames = {"workflow_id", "email"}))
public class Recipient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ConsentWorkflow workflow;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "signing_order", nullable = false)
    private int signingOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecipientStatus status = RecipientStatus.PENDING;

    private Instant respondedAt;

    protected Recipient() {}

    public Recipient(String name, String email, int signingOrder) {
        this.name = name;
        this.email = email.toLowerCase();
        this.signingOrder = signingOrder;
    }

    public Long getId() { return id; }
    public ConsentWorkflow getWorkflow() { return workflow; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getSigningOrder() { return signingOrder; }
    public RecipientStatus getStatus() { return status; }
    public Instant getRespondedAt() { return respondedAt; }
    public void setWorkflow(ConsentWorkflow workflow) { this.workflow = workflow; }
    public void setStatus(RecipientStatus status) { this.status = status; this.respondedAt = Instant.now(); }
}