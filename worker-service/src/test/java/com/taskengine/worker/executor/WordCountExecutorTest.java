package com.taskengine.worker.executor;

import com.taskengine.common.model.TaskContext;
import com.taskengine.common.model.TaskResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordCountExecutorTest {

    private final WordCountExecutor executor = new WordCountExecutor();

    @Test
    void testExecuteWordCount() throws Exception {
        // Arrange
        TaskContext context = TaskContext.builder()
                .taskId("test-task")
                .jobId("test-job")
                .taskType("WORD_COUNT")
                .inputData("hello WORLD Hello   world \n test")
                .build();

        // Act
        TaskResult result = executor.execute(context);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getResultData());
        
        // Expected JSON: {"test":1,"hello":2,"world":2}
        assertTrue(result.getResultData().contains("\"hello\":2"));
        assertTrue(result.getResultData().contains("\"world\":2"));
        assertTrue(result.getResultData().contains("\"test\":1"));
    }
}
