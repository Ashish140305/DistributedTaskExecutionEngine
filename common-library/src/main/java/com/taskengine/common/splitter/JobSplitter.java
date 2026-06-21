package com.taskengine.common.splitter;

import java.util.List;

/**
 * Strategy interface for splitting a job into multiple tasks.
 *
 * <p>Different job types require different splitting strategies.
 * For example, a word count job might split text by paragraphs,
 * while a file processing job might split by file chunks.
 *
 * <p><b>Design Pattern:</b> Strategy Pattern
 */
public interface JobSplitter {

    /**
     * Splits a job's input data into multiple task input chunks.
     *
     * @param inputData  the raw input data from the job submission
     * @return a list of input data strings, one per task to be created
     */
    List<String> split(String inputData);

    /**
     * Returns the job type this splitter handles.
     *
     * @return the job type string
     */
    String getJobType();
}
