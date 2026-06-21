package com.taskengine.worker.executor;

import com.taskengine.common.exception.TaskExecutionException;
import com.taskengine.common.executor.TaskExecutor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Factory that maps task-type strings to their concrete {@link TaskExecutor}
 * implementations.
 *
 * <p><b>Design Pattern:</b> Factory + Strategy — executors are auto-discovered
 * by Spring's dependency injection and registered in a lookup map on startup.
 * The execution engine obtains the correct executor for a given task type
 * through this factory, keeping the engine decoupled from concrete strategies.
 *
 * <p>Adding a new task type requires only creating a new {@link TaskExecutor}
 * {@code @Component}; no factory or engine changes needed.
 */
@Component
public class TaskExecutorFactory {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutorFactory.class);

    private final List<TaskExecutor> executors;
    private Map<String, TaskExecutor> executorMap;

    /**
     * Constructs the factory with all {@link TaskExecutor} beans discovered
     * by the Spring container.
     *
     * @param executors auto-wired list of all TaskExecutor implementations
     */
    public TaskExecutorFactory(List<TaskExecutor> executors) {
        this.executors = executors;
    }

    /**
     * Builds the type → executor lookup map and logs all registered types.
     * Called automatically after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        Map<String, TaskExecutor> map = new HashMap<>();
        for (TaskExecutor executor : executors) {
            String type = executor.getTaskType();
            if (map.containsKey(type)) {
                log.warn("Duplicate TaskExecutor for type '{}': {} will be replaced by {}",
                        type, map.get(type).getClass().getSimpleName(),
                        executor.getClass().getSimpleName());
            }
            map.put(type, executor);
            log.info("Registered TaskExecutor: type='{}' → {}", type, executor.getClass().getSimpleName());
        }
        this.executorMap = Collections.unmodifiableMap(map);
        log.info("TaskExecutorFactory initialised with {} executor(s): {}", executorMap.size(), executorMap.keySet());
    }

    /**
     * Retrieves the executor for the given task type.
     *
     * @param type the task type string (e.g. {@code "WORD_COUNT"})
     * @return the corresponding {@link TaskExecutor}
     * @throws TaskExecutionException if no executor is registered for the type
     */
    public TaskExecutor getExecutor(String type) {
        TaskExecutor executor = executorMap.get(type);
        if (executor == null) {
            throw new TaskExecutionException("unknown",
                    "No TaskExecutor registered for type '" + type + "'. " +
                            "Supported types: " + executorMap.keySet());
        }
        return executor;
    }

    /**
     * Returns the set of all supported task types.
     *
     * @return unmodifiable set of registered type strings
     */
    public Set<String> getSupportedTypes() {
        return executorMap.keySet();
    }
}
