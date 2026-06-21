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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates numeric data from the input.
 *
 * <p>Task type: {@code DATA_AGGREGATION}
 *
 * <p>The input is expected to contain numbers separated by commas or newlines.
 * The executor computes:
 * <ul>
 *   <li><b>sum</b> — total of all values</li>
 *   <li><b>average</b> — arithmetic mean</li>
 *   <li><b>min</b> — smallest value</li>
 *   <li><b>max</b> — largest value</li>
 *   <li><b>count</b> — number of valid values parsed</li>
 * </ul>
 *
 * <p>Non-numeric tokens are silently skipped and logged at WARN level.
 */
@Component
public class DataAggregationExecutor implements TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(DataAggregationExecutor.class);
    private static final String TASK_TYPE = "DATA_AGGREGATION";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public TaskResult execute(TaskContext context) throws Exception {
        String taskId = context.getTaskId();
        String input = context.getInputData();

        log.info("Executing DATA_AGGREGATION task [{}]", taskId);
        long startTime = System.currentTimeMillis();

        if (input == null || input.isBlank()) {
            throw new TaskExecutionException(taskId, "Input data must not be null or blank");
        }

        // Split on commas, newlines, or both
        String[] tokens = input.split("[,\\n\\r]+");
        List<Double> numbers = new ArrayList<>();
        int skippedCount = 0;

        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                numbers.add(Double.parseDouble(trimmed));
            } catch (NumberFormatException e) {
                skippedCount++;
                log.warn("Task [{}]: skipping non-numeric token '{}'", taskId, trimmed);
            }
        }

        if (numbers.isEmpty()) {
            throw new TaskExecutionException(taskId,
                    "No valid numbers found in input (skipped " + skippedCount + " tokens)");
        }

        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double value : numbers) {
            sum += value;
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        double average = sum / numbers.size();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sum", sum);
        result.put("average", average);
        result.put("min", min);
        result.put("max", max);
        result.put("count", numbers.size());

        String resultData;
        try {
            resultData = OBJECT_MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new TaskExecutionException(taskId, "Failed to serialise aggregation result", e);
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info("DATA_AGGREGATION task [{}] completed in {} ms — {} values aggregated (skipped {})",
                taskId, executionTimeMs, numbers.size(), skippedCount);

        return TaskResult.success(taskId, resultData, executionTimeMs, context.getRetryCount() + 1);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
