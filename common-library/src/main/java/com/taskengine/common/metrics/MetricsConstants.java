package com.taskengine.common.metrics;

/**
 * Centralized constants for Micrometer metric names and tags.
 *
 * <p>Ensures consistent metric naming across coordinator and worker services,
 * making Prometheus queries and Grafana dashboards reliable.
 */
public final class MetricsConstants {

    private MetricsConstants() {
        // Utility class — prevent instantiation
    }

    // ============================
    // Metric Name Prefix
    // ============================
    public static final String PREFIX = "taskengine";

    // ============================
    // Worker Metrics
    // ============================
    public static final String ACTIVE_WORKERS = PREFIX + ".workers.active";
    public static final String UNHEALTHY_WORKERS = PREFIX + ".workers.unhealthy";
    public static final String DEAD_WORKERS = PREFIX + ".workers.dead";

    // ============================
    // Task Metrics
    // ============================
    public static final String QUEUED_TASKS = PREFIX + ".tasks.queued";
    public static final String RUNNING_TASKS = PREFIX + ".tasks.running";
    public static final String COMPLETED_TASKS = PREFIX + ".tasks.completed";
    public static final String FAILED_TASKS = PREFIX + ".tasks.failed";
    public static final String RETRY_COUNT = PREFIX + ".tasks.retries";
    public static final String TASK_EXECUTION_TIME = PREFIX + ".tasks.execution.time";

    // ============================
    // Job Metrics
    // ============================
    public static final String SUBMITTED_JOBS = PREFIX + ".jobs.submitted";
    public static final String COMPLETED_JOBS = PREFIX + ".jobs.completed";
    public static final String FAILED_JOBS = PREFIX + ".jobs.failed";

    // ============================
    // Tag Keys
    // ============================
    public static final String TAG_WORKER_ID = "worker_id";
    public static final String TAG_JOB_TYPE = "job_type";
    public static final String TAG_TASK_TYPE = "task_type";
    public static final String TAG_STATUS = "status";
}
