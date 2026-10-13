package com.satya.consentflow.api;

import com.satya.consentflow.api.dto.*;
import com.satya.consentflow.domain.RecipientStatus;
import com.satya.consentflow.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {
    private final WorkflowService service;
    public WorkflowController(WorkflowService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkflowResponse create(@Valid @RequestBody CreateWorkflowRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<WorkflowResponse> list() { return service.list(); }

    @GetMapping("/{id}")
    public WorkflowResponse get(@PathVariable Long id) { return service.get(id); }

    @PostMapping("/{id}/recipients")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkflowResponse addRecipient(@PathVariable Long id, @Valid @RequestBody AddRecipientRequest request) {
        return service.addRecipient(id, request);
    }

    @PostMapping("/{id}/send")
    public WorkflowResponse send(@PathVariable Long id, @RequestParam(defaultValue = "system") String actor) {
        return service.send(id, actor);
    }

    @PostMapping("/{workflowId}/recipients/{recipientId}/response")
    public WorkflowResponse respond(@PathVariable Long workflowId, @PathVariable Long recipientId,
                                    @RequestParam RecipientStatus status) {
        return service.respond(workflowId, recipientId, status);
    }

    @PostMapping("/{id}/cancel")
    public WorkflowResponse cancel(@PathVariable Long id, @RequestParam(defaultValue = "system") String actor) {
        return service.cancel(id, actor);
    }

    @GetMapping("/{id}/audit-events")
    public List<AuditEventResponse> events(@PathVariable Long id) { return service.events(id); }
}