package com.taskengine.common.executor;

import com.taskengine.common.model.TaskContext;
import com.taskengine.common.model.TaskResult;

/**
 * Strategy interface for task execution.
 *
 * <p>Each concrete implementation handles a specific task type (e.g., word count,
 * string processing, data aggregation). New task types are added by implementing
 * this interface and registering with the {@code TaskExecutorFactory}.
 *
 * <p><b>Design Pattern:</b> Strategy Pattern — allows swapping execution algorithms
 * without modifying the execution engine.
 *
 * @see TaskContext
 * @see TaskResult
 */
public interface TaskExecutor {

    /**
     * Executes a task within the given context.
     *
     * @param context the task context containing input data and parameters
     * @return the result of the task execution
     * @throws Exception if the task execution fails
     */
    TaskResult execute(TaskContext context) throws Exception;

    /**
     * Returns the unique type identifier for this executor.
     * Must match the task type used in job submissions.
     *
     * @return the task type string (e.g., "WORD_COUNT", "STRING_PROCESSING")
     */
    String getTaskType();
}
