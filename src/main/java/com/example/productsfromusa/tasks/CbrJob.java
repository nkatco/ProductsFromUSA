package com.example.productsfromusa.tasks;

import com.example.productsfromusa.models.USD;
import com.example.productsfromusa.services.CbrService;
import com.example.productsfromusa.services.data.USDService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CbrJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(CbrJob.class);


    @Autowired
    private CbrService cbrService;
    @Autowired
    private USDService usdService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Executing CbrJob task at {}", context.getFireTime());
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
