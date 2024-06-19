package com.example.productsfromusa.tasks;

import com.example.productsfromusa.models.Anons;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.PreAnonsService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class AnonsJob extends QuartzJobBean {

    @Autowired
    private PreAnonsService preAnonsService;
    @Autowired
    private AnonsService anonsService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String anons_id = (String) dataMap.get("anons_id");
        Anons anons = anonsService.getAnonsById(anons_id);
        if(anons != null) {
            preAnonsService.anonsPost(anons);
        } else {
            System.out.println("Anons " + anons_id + " is null");
        }
    }
}
