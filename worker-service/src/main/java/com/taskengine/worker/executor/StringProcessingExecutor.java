package com.taskengine.worker.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskengine.common.exception.TaskExecutionException;
import com.taskengine.common.executor.TaskExecutor;
import com.taskengine.common.model.TaskContext;
import com.taskengine.common.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Executes string-processing tasks.
 *
 * <p>Task type: {@code STRING_PROCESSING}
 *
 * <p>Supported operations (selected via the {@code operation} parameter):
 * <ul>
 *   <li><b>UPPERCASE</b> — converts the input to upper case (default)</li>
 *   <li><b>REVERSE</b> — reverses the input string</li>
 *   <li><b>CHAR_COUNT</b> — returns a JSON object with character statistics:
 *       {@code totalChars}, {@code letters}, {@code digits}, {@code spaces}</li>
 * </ul>
 *
 * <p>A random sleep (100–500 ms) simulates real processing latency.
 */
@Component
public class StringProcessingExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(StringProcessingExecutor.class);
    private static final String TASK_TYPE = "STRING_PROCESSING";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public TaskResult execute(TaskContext context) throws Exception {
        String taskId = context.getTaskId();
        String input = context.getInputData();
        Map<String, String> params = context.getParameters();
        String operation = params.getOrDefault("operation", "UPPERCASE");

        log.info("Executing STRING_PROCESSING task [{}] with operation={}", taskId, operation);
        long startTime = System.currentTimeMillis();

        // Simulate realistic processing latency
        Thread.sleep(ThreadLocalRandom.current().nextLong(100, 501));

        if (input == null) {
            throw new TaskExecutionException(taskId, "Input data must not be null");
        }

        String resultData = switch (operation.toUpperCase()) {
            case "UPPERCASE" -> processUppercase(input);
            case "REVERSE" -> processReverse(input);
            case "CHAR_COUNT" -> processCharCount(input);
            default -> {
                log.warn("Unknown operation '{}' for task [{}], falling back to UPPERCASE", operation, taskId);
                yield processUppercase(input);
            }
        };

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info("STRING_PROCESSING task [{}] completed in {} ms", taskId, executionTimeMs);

        return TaskResult.success(taskId, resultData, executionTimeMs, context.getRetryCount() + 1);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    /**
     * Converts the input string to upper case.
     */
    private String processUppercase(String input) {
        return input.toUpperCase();
    }

    /**
     * Reverses the input string.
     */
    private String processReverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Counts characters by category and returns a JSON representation.
     */
    private String processCharCount(String input) {
        int totalChars = input.length();
        int letters = 0;
        int digits = 0;
        int spaces = 0;

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                letters++;
            } else if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isWhitespace(c)) {
                spaces++;
            }
        }

        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("totalChars", totalChars);
        stats.put("letters", letters);
        stats.put("digits", digits);
        stats.put("spaces", spaces);

        try {
            return OBJECT_MAPPER.writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new TaskExecutionException(input, "Failed to serialise CHAR_COUNT result", e);
        }
    }
}
