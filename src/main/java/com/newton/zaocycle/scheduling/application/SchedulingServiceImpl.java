package com.newton.zaocycle.scheduling.application;

import com.newton.zaocycle.scheduling.application.job.HarvestMaturityJob;
import com.newton.zaocycle.scheduling.application.job.PickupExpiryJob;
import com.newton.zaocycle.scheduling.application.job.PickupReminderJob;
import com.newton.zaocycle.scheduling.domain.model.JobGroup;
import com.newton.zaocycle.shared.util.JobDataMapHelper;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
class SchedulingServiceImpl implements SchedulingService {

    private final Scheduler scheduler;

    SchedulingServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void scheduleHarvestMaturity(UUID applicationId, Instant runAt) {
        schedule(HarvestMaturityJob.class, JobGroup.HARVEST, applicationId.toString(),
                JobDataMapHelper.of("applicationId", applicationId.toString()), runAt);
    }

    @Override
    public void schedulePickupReminder(UUID pickupId, Instant runAt) {
        schedule(PickupReminderJob.class, JobGroup.PICKUP_REMINDER, pickupId.toString(),
                JobDataMapHelper.of("pickupId", pickupId.toString()), runAt);
    }

    @Override
    public void schedulePickupExpiry(UUID pickupId, Instant runAt) {
        schedule(PickupExpiryJob.class, JobGroup.PICKUP_EXPIRY, pickupId.toString(),
                JobDataMapHelper.of("pickupId", pickupId.toString()), runAt);
    }

    @Override
    public void cancelHarvestMaturity(UUID applicationId) {
        deleteJob(JobKey.jobKey(applicationId.toString(), JobGroup.HARVEST.value()));
    }

    @Override
    public void cancelPickupJobs(UUID pickupId) {
        deleteJob(JobKey.jobKey(pickupId.toString(), JobGroup.PICKUP_REMINDER.value()));
        deleteJob(JobKey.jobKey(pickupId.toString(), JobGroup.PICKUP_EXPIRY.value()));
    }

    private void schedule(Class<? extends Job> jobClass, JobGroup group,
                          String name, JobDataMap dataMap, Instant runAt) {
        JobKey jobKey = JobKey.jobKey(name, group.value());
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group.value());

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .usingJobData(dataMap)
                .storeDurably(false)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobDetail)
                .startAt(Date.from(runAt))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to schedule job " + jobKey, e);
        }
    }

    private void deleteJob(JobKey jobKey) {
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to cancel job " + jobKey, e);
        }
    }
}
