package com.satya.consentflow.repo;

import com.satya.consentflow.domain.ConsentWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<ConsentWorkflow, Long> {}