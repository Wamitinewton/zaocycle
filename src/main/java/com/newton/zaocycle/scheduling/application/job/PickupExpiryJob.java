package com.newton.zaocycle.scheduling.application.job;

import com.newton.zaocycle.scheduling.application.event.PickupExpired;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

public class PickupExpiryJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        UUID pickupId = UUID.fromString(context.getMergedJobDataMap().getString("pickupId"));

        ApplicationContext appCtx = JobContextUtil.applicationContext(context);
        appCtx.publishEvent(new PickupExpired(pickupId));
    }
}
