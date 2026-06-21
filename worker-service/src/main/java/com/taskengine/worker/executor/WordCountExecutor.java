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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Counts the frequency of each word in the input text.
 *
 * <p>Task type: {@code WORD_COUNT}
 *
 * <p>The input is split on whitespace. Word comparison is case-insensitive
 * (all words are lower-cased). The result is a JSON map of
 * {@code word → frequency} sorted alphabetically.
 *
 * <p>A random sleep (200–800 ms) simulates real processing latency.
 */
@Component
public class WordCountExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(WordCountExecutor.class);
    private static final String TASK_TYPE = "WORD_COUNT";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public TaskResult execute(TaskContext context) throws Exception {
        String taskId = context.getTaskId();
        String input = context.getInputData();

        log.info("Executing WORD_COUNT task [{}], input length={}", taskId,
                input != null ? input.length() : 0);
        long startTime = System.currentTimeMillis();

        // Simulate realistic processing latency
        Thread.sleep(ThreadLocalRandom.current().nextLong(200, 801));

        if (input == null || input.isBlank()) {
            throw new TaskExecutionException(taskId, "Input data must not be null or blank");
        }

        // Split on whitespace and count frequencies (case-insensitive)
        Map<String, Integer> frequencies = new TreeMap<>();
        String[] words = input.trim().split("\\s+");
        for (String word : words) {
            String normalised = word.toLowerCase();
            frequencies.merge(normalised, 1, Integer::sum);
        }

        String resultData;
        try {
            resultData = OBJECT_MAPPER.writeValueAsString(frequencies);
        } catch (JsonProcessingException e) {
            throw new TaskExecutionException(taskId, "Failed to serialise word-count result", e);
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info("WORD_COUNT task [{}] completed in {} ms — {} unique words found",
                taskId, executionTimeMs, frequencies.size());

        return TaskResult.success(taskId, resultData, executionTimeMs, context.getRetryCount() + 1);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
