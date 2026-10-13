package com.satya.consentflow.service;

import com.satya.consentflow.api.dto.*;
import com.satya.consentflow.domain.*;
import com.satya.consentflow.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WorkflowService {
    private final WorkflowRepository workflows;
    private final RecipientRepository recipients;
    private final AuditEventRepository audit;

    public WorkflowService(WorkflowRepository workflows, RecipientRepository recipients, AuditEventRepository audit) {
        this.workflows = workflows;
        this.recipients = recipients;
        this.audit = audit;
    }

    @Transactional
    public WorkflowResponse create(CreateWorkflowRequest request) {
        ConsentWorkflow workflow = workflows.save(new ConsentWorkflow(request.title().trim(), request.createdBy().trim()));
        record(workflow, "WORKFLOW_CREATED", request.createdBy(), "Created workflow: " + request.title());
        return WorkflowResponse.from(workflow);
    }

    @Transactional(readOnly = true)
    public List<WorkflowResponse> list() {
        return workflows.findAll().stream().map(WorkflowResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public WorkflowResponse get(Long id) {
        return WorkflowResponse.from(requireWorkflow(id));
    }

    @Transactional
    public WorkflowResponse addRecipient(Long id, AddRecipientRequest request) {
        ConsentWorkflow workflow = requireWorkflow(id);
        if (workflow.getStatus() != WorkflowStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Recipients can only be added while workflow is DRAFT");
        }
        boolean duplicate = workflow.getRecipients().stream()
            .anyMatch(r -> r.getEmail().equalsIgnoreCase(request.email()));
        if (duplicate) throw new ResponseStatusException(HttpStatus.CONFLICT, "Recipient email already exists in this workflow");
        Recipient recipient = new Recipient(request.name().trim(), request.email().trim(), request.signingOrder());
        workflow.addRecipient(recipient);
        workflows.save(workflow);
        record(workflow, "RECIPIENT_ADDED", workflow.getCreatedBy(), "Added recipient " + recipient.getEmail());
        return WorkflowResponse.from(workflow);
    }

    @Transactional
    public WorkflowResponse send(Long id, String actor) {
        ConsentWorkflow workflow = requireWorkflow(id);
        if (workflow.getStatus() != WorkflowStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT workflows can be sent");
        if (workflow.getRecipients().isEmpty())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Add at least one recipient before sending");
        workflow.setStatus(WorkflowStatus.SENT);
        record(workflow, "WORKFLOW_SENT", actor, "Workflow sent to " + workflow.getRecipients().size() + " recipient(s)");
        return WorkflowResponse.from(workflows.save(workflow));
    }

    @Transactional
    public WorkflowResponse respond(Long workflowId, Long recipientId, RecipientStatus newStatus) {
        ConsentWorkflow workflow = requireWorkflow(workflowId);
        if (workflow.getStatus() != WorkflowStatus.SENT && workflow.getStatus() != WorkflowStatus.IN_PROGRESS)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Workflow is not accepting responses");
        Recipient recipient = recipients.findByIdAndWorkflowId(recipientId, workflowId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));
        if (newStatus != RecipientStatus.VIEWED && newStatus != RecipientStatus.CONSENTED && newStatus != RecipientStatus.DECLINED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported recipient transition");
        if (recipient.getStatus() == RecipientStatus.CONSENTED || recipient.getStatus() == RecipientStatus.DECLINED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Recipient has already submitted a final response");
        recipient.setStatus(newStatus);
        if (newStatus == RecipientStatus.CONSENTED || newStatus == RecipientStatus.DECLINED) {
            boolean allFinal = workflow.getRecipients().stream().allMatch(r ->
                r.getStatus() == RecipientStatus.CONSENTED || r.getStatus() == RecipientStatus.DECLINED);
            workflow.setStatus(allFinal ? WorkflowStatus.COMPLETED : WorkflowStatus.IN_PROGRESS);
        } else {
            workflow.setStatus(WorkflowStatus.IN_PROGRESS);
        }
        recipients.save(recipient);
        workflows.save(workflow);
        record(workflow, "RECIPIENT_" + newStatus.name(), recipient.getEmail(), "Recipient status changed to " + newStatus);
        return WorkflowResponse.from(workflow);
    }

    @Transactional
    public WorkflowResponse cancel(Long id, String actor) {
        ConsentWorkflow workflow = requireWorkflow(id);
        if (workflow.getStatus() == WorkflowStatus.COMPLETED || workflow.getStatus() == WorkflowStatus.CANCELLED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Completed or cancelled workflow cannot be cancelled");
        workflow.setStatus(WorkflowStatus.CANCELLED);
        record(workflow, "WORKFLOW_CANCELLED", actor, "Workflow cancelled");
        return WorkflowResponse.from(workflows.save(workflow));
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> events(Long id) {
        requireWorkflow(id);
        return audit.findByWorkflowIdOrderByOccurredAtAsc(id).stream().map(AuditEventResponse::from).toList();
    }

    private ConsentWorkflow requireWorkflow(Long id) {
        return workflows.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow not found"));
    }

    private void record(ConsentWorkflow workflow, String type, String actor, String details) {
        audit.save(new AuditEvent(workflow.getId(), type, actor, details));
    }
}