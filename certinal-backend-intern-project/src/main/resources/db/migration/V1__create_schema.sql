CREATE TABLE consent_workflows (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    created_by VARCHAR(180) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE workflow_recipients (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES consent_workflows(id) ON DELETE CASCADE,
    name VARCHAR(180) NOT NULL,
    email VARCHAR(255) NOT NULL,
    signing_order INTEGER NOT NULL CHECK (signing_order >= 1),
    status VARCHAR(30) NOT NULL,
    responded_at TIMESTAMPTZ,
    CONSTRAINT uk_workflow_recipient_email UNIQUE (workflow_id, email)
);

CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES consent_workflows(id) ON DELETE CASCADE,
    event_type VARCHAR(80) NOT NULL,
    actor VARCHAR(180) NOT NULL,
    details VARCHAR(1000),
    occurred_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_workflow_status ON consent_workflows(status);
CREATE INDEX idx_recipient_workflow ON workflow_recipients(workflow_id);
CREATE INDEX idx_audit_workflow_time ON audit_events(workflow_id, occurred_at);
