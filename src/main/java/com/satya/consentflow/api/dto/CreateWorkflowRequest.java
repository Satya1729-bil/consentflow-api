package com.satya.consentflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkflowRequest(
    @NotBlank @Size(max = 180) String title,
    @NotBlank @Size(max = 180) String createdBy
) {}