-- ============================================================
-- V1__init_schema.sql
-- Initial schema for the Distributed Task Execution Engine
-- Coordinator Service database.
-- ============================================================

-- Workers table: tracks registered worker nodes
CREATE TABLE IF NOT EXISTS workers (
    id                  UUID        PRIMARY KEY,
    hostname            VARCHAR(255) NOT NULL,
    port                INTEGER      NOT NULL,
    status              VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    last_heartbeat      TIMESTAMP    NOT NULL DEFAULT NOW(),
    running_tasks       INTEGER      NOT NULL DEFAULT 0,
    max_concurrent_tasks INTEGER    NOT NULL DEFAULT 4,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Jobs table: top-level job records submitted by clients
CREATE TABLE IF NOT EXISTS jobs (
    id                  UUID        PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    type                VARCHAR(255) NOT NULL,
    status              VARCHAR(50)  NOT NULL DEFAULT 'SUBMITTED',
    input_data          TEXT,
    total_tasks         INTEGER      NOT NULL DEFAULT 0,
    completed_tasks     INTEGER      NOT NULL DEFAULT 0,
    failed_tasks        INTEGER      NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    completed_at        TIMESTAMP
);

-- Tasks table: individual units of work derived from a job
CREATE TABLE IF NOT EXISTS tasks (
    id                  UUID        PRIMARY KEY,
    job_id              UUID        NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    worker_id           UUID        REFERENCES workers(id) ON DELETE SET NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    type                VARCHAR(255) NOT NULL,
    input_data          TEXT,
    result_data         TEXT,
    retry_count         INTEGER     NOT NULL DEFAULT 0,
    max_retries         INTEGER     NOT NULL DEFAULT 3,
    error_message       TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    started_at          TIMESTAMP,
    completed_at        TIMESTAMP
);

-- Task results table: detailed outcome of each task attempt
CREATE TABLE IF NOT EXISTS task_results (
    id                  UUID        PRIMARY KEY,
    task_id             UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    result_data         TEXT,
    execution_time_ms   BIGINT,
    attempt_number      INTEGER     NOT NULL DEFAULT 1,
    status              VARCHAR(50) NOT NULL,
    error_message       TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Indexes for query performance
-- ============================================================

-- Workers indexes
CREATE INDEX idx_workers_status ON workers(status);
CREATE INDEX idx_workers_last_heartbeat ON workers(last_heartbeat);

-- Jobs indexes
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_created_at ON jobs(created_at);
CREATE INDEX idx_jobs_type ON jobs(type);

-- Tasks indexes
CREATE INDEX idx_tasks_job_id ON tasks(job_id);
CREATE INDEX idx_tasks_worker_id ON tasks(worker_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_job_id_status ON tasks(job_id, status);
CREATE INDEX idx_tasks_worker_id_status ON tasks(worker_id, status);
CREATE INDEX idx_tasks_type ON tasks(type);

-- Task results indexes
CREATE INDEX idx_task_results_task_id ON task_results(task_id);
CREATE INDEX idx_task_results_status ON task_results(status);
