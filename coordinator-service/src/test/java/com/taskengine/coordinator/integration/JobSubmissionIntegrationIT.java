package com.taskengine.coordinator.integration;

import com.taskengine.common.dto.JobStatusResponse;
import com.taskengine.common.dto.JobSubmitRequest;
import com.taskengine.common.model.JobStatus;
import com.taskengine.coordinator.persistence.repository.JobRepository;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class JobSubmissionIntegrationIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testSubmitJobIntegration() {
        // Arrange
        JobSubmitRequest request = new JobSubmitRequest();
        request.setName("Integration Test Job");
        request.setType("WORD_COUNT");
        request.setInputData("hello world\nintegration test");

        // Act
        ResponseEntity<JobStatusResponse> responseEntity = restTemplate.postForEntity(
                "/api/jobs", request, JobStatusResponse.class);

        // Assert HTTP response
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        
        JobStatusResponse response = responseEntity.getBody();
        assertNotNull(response.getJobId());
        assertEquals("Integration Test Job", response.getName());
        assertEquals(2, response.getTotalTasks());
        assertEquals(JobStatus.RUNNING, response.getStatus());

        // Assert Database State
        assertEquals(1, jobRepository.count());
        assertEquals(2, taskRepository.count());
    }
}
