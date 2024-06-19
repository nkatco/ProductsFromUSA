package com.example.productsfromusa.tasks;

import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.services.TokenAnonsService;
import com.example.productsfromusa.services.data.TokenService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.*;

public class TokenJob extends QuartzJobBean {

    @Autowired
    private TokenAnonsService tokenAnonsService;
    @Autowired
    private TokenService tokenService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String token_id = (String) dataMap.get("token_id");
        Token token = tokenService.getTokenById(token_id);
        if(token != null) {
            tokenAnonsService.updateToken(token);
        } else {
            System.out.println("Token " + token_id + " is null");
        }
    }
}
