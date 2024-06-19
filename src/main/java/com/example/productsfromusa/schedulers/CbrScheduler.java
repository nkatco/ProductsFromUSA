package com.example.productsfromusa.schedulers;

import com.example.productsfromusa.models.USD;
import com.example.productsfromusa.services.CbrService;
import com.example.productsfromusa.services.data.USDService;
import com.example.productsfromusa.tasks.CbrJob;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CbrScheduler {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private CbrService cbrService;
    @Autowired
    private USDService usdService;

    @PostConstruct
    public void init() throws SchedulerException {
        if(!createJobUSDIfNotExists()) {
            System.out.println("Выполнение задания CbrJob");
            USD usd = usdService.getCourse();
            if(usd != null) {
                usd.setCourse(cbrService.getCourseUSD());
                usdService.save(usd);
            } else {
                usd = new USD();
                usd.setCourse(cbrService.getCourseUSD());
                usdService.save(usd);
            }
        }
    }

    public boolean createJobUSDIfNotExists() throws SchedulerException {
        JobKey jobKey = new JobKey("USD", "cbr");

        if (!scheduler.checkExists(jobKey)) {
            JobDetail jobDetail = JobBuilder.newJob(CbrJob.class)
                    .withIdentity(jobKey)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity("USD" + "Trigger", "cbr")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(8, 0))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            return false;
        }
        return true;
    }
}
