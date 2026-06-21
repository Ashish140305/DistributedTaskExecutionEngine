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
 * Simulates file processing by analysing the input text as if it were
 * the contents of a file.
 *
 * <p>Task type: {@code FILE_PROCESSING}
 *
 * <p>Statistics produced:
 * <ul>
 *   <li><b>totalLines</b> — number of lines in the input</li>
 *   <li><b>nonEmptyLines</b> — lines that are not blank</li>
 *   <li><b>totalWords</b> — total word count across all lines</li>
 *   <li><b>longestLine</b> — content of the longest line</li>
 *   <li><b>longestLineLength</b> — character count of the longest line</li>
 *   <li><b>averageLineLength</b> — average number of characters per line</li>
 * </ul>
 *
 * <p>A random sleep (300–1000 ms) simulates file I/O latency.
 */
@Component
public class FileProcessingExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingExecutor.class);
    private static final String TASK_TYPE = "FILE_PROCESSING";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public TaskResult execute(TaskContext context) throws Exception {
        String taskId = context.getTaskId();
        String input = context.getInputData();

        log.info("Executing FILE_PROCESSING task [{}]", taskId);
        long startTime = System.currentTimeMillis();

        // Simulate file I/O latency
        Thread.sleep(ThreadLocalRandom.current().nextLong(300, 1001));

        if (input == null) {
            throw new TaskExecutionException(taskId, "Input data must not be null");
        }

        String[] lines = input.split("\\r?\\n", -1);
        int totalLines = lines.length;
        int nonEmptyLines = 0;
        int totalWords = 0;
        String longestLine = "";
        long totalCharacters = 0;

        for (String line : lines) {
            if (!line.isBlank()) {
                nonEmptyLines++;
                // Count words in non-empty lines
                String[] words = line.trim().split("\\s+");
                totalWords += words.length;
            }
            totalCharacters += line.length();
            if (line.length() > longestLine.length()) {
                longestLine = line;
            }
        }

        double averageLineLength = totalLines > 0 ? (double) totalCharacters / totalLines : 0.0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalLines", totalLines);
        stats.put("nonEmptyLines", nonEmptyLines);
        stats.put("totalWords", totalWords);
        stats.put("longestLine", longestLine);
        stats.put("longestLineLength", longestLine.length());
        stats.put("averageLineLength", Math.round(averageLineLength * 100.0) / 100.0);

        String resultData;
        try {
            resultData = OBJECT_MAPPER.writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new TaskExecutionException(taskId, "Failed to serialise file-processing result", e);
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info("FILE_PROCESSING task [{}] completed in {} ms — {} lines, {} words",
                taskId, executionTimeMs, totalLines, totalWords);

        return TaskResult.success(taskId, resultData, executionTimeMs, context.getRetryCount() + 1);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
