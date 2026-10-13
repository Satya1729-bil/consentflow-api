package com.satya.consentflow.api.dto;

import com.satya.consentflow.domain.ConsentWorkflow;
import com.satya.consentflow.domain.WorkflowStatus;
import java.time.Instant;
import java.util.List;

public record WorkflowResponse(Long id, String title, String createdBy, WorkflowStatus status,
                               Instant createdAt, Instant updatedAt, List<RecipientResponse> recipients) {
    public static WorkflowResponse from(ConsentWorkflow w) {
        return new WorkflowResponse(w.getId(), w.getTitle(), w.getCreatedBy(), w.getStatus(),
            w.getCreatedAt(), w.getUpdatedAt(), w.getRecipients().stream().map(RecipientResponse::from).toList());
    }
}