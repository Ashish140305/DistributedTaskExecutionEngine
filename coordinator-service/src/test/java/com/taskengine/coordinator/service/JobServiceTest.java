package com.taskengine.coordinator.service;

import com.taskengine.common.dto.JobStatusResponse;
import com.taskengine.common.dto.JobSubmitRequest;
import com.taskengine.common.model.JobStatus;
import com.taskengine.coordinator.persistence.entity.JobEntity;
import com.taskengine.coordinator.persistence.repository.JobRepository;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import com.taskengine.coordinator.splitter.DefaultJobSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock private JobRepository jobRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskService taskService;
    @Mock private ApplicationEventPublisher eventPublisher;

    private DefaultJobSplitter jobSplitter;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobSplitter = new DefaultJobSplitter();
        jobService = new JobService(jobRepository, taskRepository, jobSplitter, taskService, eventPublisher);
    }

    @Test
    void testSubmitJob() {
        // Arrange
        JobSubmitRequest request = new JobSubmitRequest();
        request.setName("Test Job");
        request.setType("WORD_COUNT");
        request.setInputData("line1\nline2");

        when(jobRepository.save(any(JobEntity.class))).thenAnswer(invocation -> {
            JobEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return entity;
        });

        // Act
        JobStatusResponse response = jobService.submitJob(request);

        // Assert
        assertNotNull(response.getJobId());
        assertEquals("Test Job", response.getName());
        assertEquals("WORD_COUNT", response.getType());
        assertEquals(JobStatus.RUNNING, response.getStatus());
        assertEquals(2, response.getTotalTasks()); // Because of 2 lines

        verify(taskService, times(1)).createTasksForJob(any(JobEntity.class), eq(List.of("line1", "line2")));
        verify(jobRepository, atLeast(2)).save(any(JobEntity.class)); // Initial save + status update save
    }
}
