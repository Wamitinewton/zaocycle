package com.newton.zaocycle.scheduling.application.job;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

final class JobContextUtil {

    private JobContextUtil() {
    }

    static ApplicationContext applicationContext(JobExecutionContext ctx) {
        try {
            return (ApplicationContext) ctx.getScheduler().getContext().get("applicationContext");
        } catch (SchedulerException e) {
            throw new IllegalStateException("Cannot retrieve ApplicationContext from Quartz SchedulerContext", e);
        }
    }
}
