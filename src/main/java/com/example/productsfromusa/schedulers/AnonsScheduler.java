package com.example.productsfromusa.schedulers;

import com.example.productsfromusa.models.Anons;
import com.example.productsfromusa.tasks.AnonsJob;
import com.example.productsfromusa.tasks.PreAnonsJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class AnonsScheduler {
    @Autowired
    private Scheduler scheduler;

    public void scheduleCheckAnonsTask(String triggerGroup, LocalDateTime executionTime, Anons anons) throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String triggerId = UUID.randomUUID().toString();
        String jobGroup = UUID.randomUUID().toString();
        triggerGroup = UUID.randomUUID().toString();
        JobDetail jobDetail = JobBuilder.newJob(PreAnonsJob.class)
                .withIdentity(jobId, jobGroup)
                .usingJobData("anons_id", anons.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerId, triggerGroup)
                .startAt(Date.from(executionTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    public void scheduleAnonsTask(String triggerGroup, LocalDateTime executionTime, Anons anons) throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String triggerId = UUID.randomUUID().toString();
        String jobGroup = UUID.randomUUID().toString();
        triggerGroup = UUID.randomUUID().toString();
        JobDetail jobDetail = JobBuilder.newJob(AnonsJob.class)
                .withIdentity(jobId, jobGroup)
                .usingJobData("anons_id", anons.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerId, triggerGroup)
                .startAt(Date.from(executionTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
