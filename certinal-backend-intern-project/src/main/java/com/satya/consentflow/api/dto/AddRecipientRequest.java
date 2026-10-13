package com.satya.consentflow.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddRecipientRequest(
    @NotBlank @Size(max = 180) String name,
    @NotBlank @Email @Size(max = 255) String email,
    @Min(1) int signingOrder
) {}