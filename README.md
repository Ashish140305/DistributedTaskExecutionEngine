# Distributed Task Execution Engine

A production-quality **Distributed Task Execution Engine** built with Java 21, Spring Boot 3, and gRPC. The system resembles a simplified combination of Google MapReduce, Apache Spark, and Kubernetes Job Scheduling.

## Architecture

```
                    ┌─────────────────────────────┐
                    │      REST Client            │
                    │  POST /api/jobs              │
                    │  GET  /api/jobs/{id}         │
                    └─────────────┬───────────────┘
                                  │
                    ┌─────────────▼───────────────┐
                    │    Coordinator Service       │
                    │    (Master Node)             │
                    │                              │
                    │  • Job Management            │
                    │  • Task Scheduling           │
                    │  • Worker Registry           │
                    │  • Heartbeat Monitoring      │
                    │  • Fault Recovery            │
                    │                              │
                    │  REST: 8080 | gRPC: 9090     │
                    └──┬──────────┬──────────┬─────┘
                       │          │          │
              ┌────────▼──┐ ┌────▼─────┐ ┌──▼────────┐
              │ Worker-1   │ │ Worker-2  │ │ Worker-3   │
              │ gRPC:9091  │ │ gRPC:9092 │ │ gRPC:9093  │
              │ HTTP:8081  │ │ HTTP:8082 │ │ HTTP:8083  │
              └────────────┘ └──────────┘ └────────────┘
                       │          │          │
                    ┌──▼──────────▼──────────▼─────┐
                    │       PostgreSQL               │
                    │       Port: 5432               │
                    └──────────────────────────────┘
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.15 |
| Communication | gRPC 1.82.0, Protocol Buffers 4.35.1 |
| Database | PostgreSQL 16 |
| Migration | Flyway |
| Monitoring | Micrometer + Prometheus + Grafana |
| Deployment | Docker + Docker Compose |
| Testing | JUnit 5 + Mockito + Testcontainers |

## Quick Start

### Prerequisites
- Docker & Docker Compose v2
- Java 21 (for local development)
- Maven 3.9+ (for local development)

### Run with Docker Compose

```bash
cd docker
docker compose up --build
```

This starts:
- **PostgreSQL** on port 5432
- **Coordinator** on ports 8080 (REST) and 9090 (gRPC)
- **3 Workers** on ports 8081-8083 (REST) and 9091-9093 (gRPC)
- **Prometheus** on port 9099
- **Grafana** on port 3000 (admin/admin)

### Submit a Job

```bash
# Word Count job
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Word Count Job",
    "type": "WORD_COUNT",
    "inputData": "hello world hello\nfoo bar baz\nhello world foo"
  }'

# String Processing job
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "String Processing Job",
    "type": "STRING_PROCESSING",
    "inputData": "hello world\nfoo bar\nbaz qux",
    "parameters": {"operation": "UPPERCASE"}
  }'

# Data Aggregation job
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Data Aggregation Job",
    "type": "DATA_AGGREGATION",
    "inputData": "10,20,30,40,50\n100,200,300\n1,2,3,4,5"
  }'
```

### Check Job Status

```bash
curl http://localhost:8080/api/jobs/{job-id}
```

### List All Jobs

```bash
curl http://localhost:8080/api/jobs
```

### Cancel a Job

```bash
curl -X DELETE http://localhost:8080/api/jobs/{job-id}
```

## Project Structure

```
distributed-task-engine/
├── shared-proto/          # Protobuf definitions & generated gRPC code
├── common-library/        # Shared models, interfaces, DTOs, exceptions
├── coordinator-service/   # Master node (REST + gRPC server)
├── worker-service/        # Worker node (gRPC server + client)
├── monitoring/            # Prometheus & Grafana configuration
├── docker/                # Docker Compose deployment
└── docs/                  # Documentation
```

## Design Patterns

| Pattern | Usage |
|---|---|
| **Strategy** | Task executors, scheduling algorithms, job splitters |
| **Factory** | TaskExecutorFactory creates executors by type |
| **Builder** | TaskContext, TaskResult construction |
| **Observer** | Job state change events |
| **Repository** | Spring Data JPA repositories |
| **Singleton** | Worker registry, heartbeat monitor (Spring beans) |

## Fault Tolerance

- **Heartbeat**: Workers send heartbeat every 5s
- **UNHEALTHY**: No heartbeat for 10s → worker marked unhealthy
- **DEAD**: No heartbeat for 15s → worker marked dead, tasks reassigned
- **Task Retry**: Failed tasks retried up to 3 times
- **Coordinator Recovery**: State restored from PostgreSQL on restart

## Monitoring

- **Prometheus**: http://localhost:9099
- **Grafana**: http://localhost:3000 (admin/admin)
- Pre-configured dashboard with 14 panels covering workers, tasks, jobs, and throughput

## Local Development

```bash
# Build all modules
mvn clean install

# Run coordinator (requires PostgreSQL running)
cd coordinator-service
mvn spring-boot:run

# Run worker (requires coordinator running)
cd worker-service
mvn spring-boot:run
```

## Testing

```bash
# Unit tests
mvn test

# Integration tests (requires Docker for Testcontainers)
mvn verify
```
