package com.example.productsfromusa.schedulers;

import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.tasks.PreTokenJob;
import com.example.productsfromusa.tasks.TokenJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenScheduler {
    @Autowired
    private Scheduler scheduler;

    public void scheduleTokenTask(String triggerGroup, LocalDateTime executionTime, Token token) throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String triggerId = UUID.randomUUID().toString();
        JobDetail jobDetail = JobBuilder.newJob(TokenJob.class)
                .withIdentity(jobId, "group1")
                .usingJobData("token_id", token.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerId, triggerGroup)
                .startAt(Date.from(executionTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    public void schedulePreTokenTask(String triggerGroup, LocalDateTime executionTime, Token token) throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String triggerId = UUID.randomUUID().toString();
        JobDetail jobDetail = JobBuilder.newJob(PreTokenJob.class)
                .withIdentity(jobId, "group2")
                .usingJobData("token_id", token.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerId, triggerGroup)
                .startAt(Date.from(executionTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
