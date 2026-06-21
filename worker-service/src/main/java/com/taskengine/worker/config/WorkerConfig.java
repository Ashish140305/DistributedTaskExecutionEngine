package com.taskengine.worker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Infrastructure bean definitions for the worker node.
 *
 * <p>Creates a fixed-size {@link ThreadPoolExecutor} for concurrent task
 * execution and a single-thread {@link ScheduledExecutorService} for the
 * heartbeat loop.
 *
 * <p>Thread pools are sized from {@link WorkerProperties} so operators
 * can tune concurrency without a code change.
 */
@Configuration
public class WorkerConfig {

    private static final Logger log = LoggerFactory.getLogger(WorkerConfig.class);

    /**
     * Thread pool used to execute incoming tasks concurrently.
     *
     * <p>Core and max size are set to {@code worker.max-concurrent-tasks}
     * (default 4). Idle threads are kept alive for 60 seconds before
     * being reclaimed. A {@link LinkedBlockingQueue} provides unbounded
     * buffering for bursts that exceed the pool size.
     *
     * @param properties worker configuration properties
     * @return configured {@link ThreadPoolExecutor}
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor taskExecutorPool(WorkerProperties properties) {
        int poolSize = properties.getMaxConcurrentTasks();

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "task-executor-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory
        );

        log.info("Task executor thread-pool initialised: coreSize={}, maxSize={}, keepAlive=60s",
                poolSize, poolSize);
        return executor;
    }

    /**
     * Single-threaded scheduled executor used for the periodic heartbeat
     * sent to the coordinator.
     *
     * @return configured {@link ScheduledExecutorService}
     */
    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService heartbeatScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "heartbeat-scheduler");
            thread.setDaemon(true);
            return thread;
        });

        log.info("Heartbeat scheduler initialised (single-threaded)");
        return scheduler;
    }
}
