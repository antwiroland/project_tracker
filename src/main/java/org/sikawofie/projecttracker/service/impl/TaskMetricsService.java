package org.sikawofie.projecttracker.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class TaskMetricsService {
    private final  Counter tasksProcessedCounter;

    public TaskMetricsService(MeterRegistry meterRegistry){
        this.tasksProcessedCounter = Counter.builder("tasks.processed")
                .description("Total number of tasks processed")
                .register(meterRegistry);
    }

    public void processTasks(){
        tasksProcessedCounter.increment();
        System.out.println("Total number of tasks processed: " + tasksProcessedCounter.count());
    }
}
