package com.parser.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class PerformanceMonitor {

    private final ConcurrentHashMap<String, AtomicLong> callCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> totalTimes = new ConcurrentHashMap<>();

    public void recordExecutionTime(String operation, Duration duration) {
        callCounts.computeIfAbsent(operation, k -> new AtomicLong(0)).incrementAndGet();
        totalTimes.computeIfAbsent(operation, k -> new AtomicLong(0)).addAndGet(duration.toMillis());

        if (duration.toMillis() > 1000) { // Логируем медленные операции
            log.warn("Slow operation detected: {} took {}ms", operation, duration.toMillis());
        }
    }

    public void logStatistics() {
        callCounts.forEach((operation, count) -> {
            long totalTime = totalTimes.get(operation).get();
            long avgTime = count.get() > 0 ? totalTime / count.get() : 0;
            log.info("Operation: {}, Calls: {}, Total time: {}ms, Avg time: {}ms",
                    operation, count.get(), totalTime, avgTime);
        });
    }

    public Timer startTimer(String operation) {
        return new Timer(operation);
    }

    public static class Timer {
        private final String operation;
        private final Instant start;

        public Timer(String operation) {
            this.operation = operation;
            this.start = Instant.now();
        }

        public void stop() {
            Duration duration = Duration.between(start, Instant.now());
            log.debug("Operation '{}' took {} ms", operation, duration.toMillis());
        }
    }
} 