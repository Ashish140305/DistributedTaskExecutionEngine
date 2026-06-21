package com.taskengine.coordinator.splitter;

import com.taskengine.common.splitter.JobSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Default job splitter that decomposes job input by newlines.
 *
 * <p>Each non-empty, non-blank line in the input becomes an individual task.
 * This provides a simple, line-oriented splitting strategy suitable for
 * batch processing workloads where each line represents an independent
 * unit of work (e.g., a URL to crawl, a file to process, a record to transform).
 *
 * <p>Example: Given input {@code "task1\ntask2\n\ntask3"}, produces
 * three tasks with inputs {@code ["task1", "task2", "task3"]}.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Component
public class DefaultJobSplitter implements JobSplitter {

    private static final Logger log = LoggerFactory.getLogger(DefaultJobSplitter.class);

    /**
     * Splits the job input into individual task inputs by newlines.
     *
     * <p>Empty and blank lines are filtered out. Each remaining line is
     * trimmed of leading and trailing whitespace.
     *
     * @param inputData the raw job input data
     * @return list of individual task input strings, never null or empty
     * @throws IllegalArgumentException if inputData is null or blank
     */
    @Override
    public List<String> split(String inputData) {
        if (inputData == null || inputData.isBlank()) {
            throw new IllegalArgumentException("Job input data must not be null or blank");
        }

        List<String> chunks = Arrays.stream(inputData.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("Job input data produced no valid task chunks after splitting");
        }

        log.info("Split job input into {} task(s)", chunks.size());
        log.debug("Task chunks: {}", chunks);

        return chunks;
    }

    /**
     * Returns the job type this splitter handles.
     *
     * @return {@code "DEFAULT"} — the fallback splitter for all job types
     */
    @Override
    public String getJobType() {
        return "DEFAULT";
    }
}
