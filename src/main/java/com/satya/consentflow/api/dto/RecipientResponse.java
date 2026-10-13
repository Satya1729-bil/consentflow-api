package com.satya.consentflow.api.dto;

import com.satya.consentflow.domain.Recipient;
import com.satya.consentflow.domain.RecipientStatus;
import java.time.Instant;

public record RecipientResponse(Long id, String name, String email, int signingOrder,
                                RecipientStatus status, Instant respondedAt) {
    public static RecipientResponse from(Recipient r) {
        return new RecipientResponse(r.getId(), r.getName(), r.getEmail(), r.getSigningOrder(),
                                    r.getStatus(), r.getRespondedAt());
    }
}