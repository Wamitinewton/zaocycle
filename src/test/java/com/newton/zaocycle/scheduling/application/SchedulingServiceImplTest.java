package com.newton.zaocycle.scheduling.application;

import com.newton.zaocycle.scheduling.application.job.HarvestMaturityJob;
import com.newton.zaocycle.scheduling.application.job.PickupExpiryJob;
import com.newton.zaocycle.scheduling.application.job.PickupReminderJob;
import com.newton.zaocycle.scheduling.domain.model.JobGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceImplTest {

    @Mock
    private Scheduler scheduler;

    private SchedulingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SchedulingServiceImpl(scheduler);
    }

    @Test
    void scheduleHarvestMaturity_schedulesJobWithCorrectKey() throws SchedulerException {
        UUID applicationId = UUID.randomUUID();
        Instant runAt = Instant.now().plusSeconds(60);

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        service.scheduleHarvestMaturity(applicationId, runAt);

        ArgumentCaptor<JobDetail> jobCaptor = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler).scheduleJob(jobCaptor.capture(), triggerCaptor.capture());

        JobDetail job = jobCaptor.getValue();
        assertThat(job.getKey().getName()).isEqualTo(applicationId.toString());
        assertThat(job.getKey().getGroup()).isEqualTo(JobGroup.HARVEST.value());
        assertThat(job.getJobClass()).isEqualTo(HarvestMaturityJob.class);
        assertThat(job.getJobDataMap().getString("applicationId")).isEqualTo(applicationId.toString());

        Trigger trigger = triggerCaptor.getValue();
        assertThat(trigger.getStartTime()).isEqualTo(Date.from(runAt));
    }

    @Test
    void schedulePickupReminder_schedulesJobWithCorrectKey() throws SchedulerException {
        UUID pickupId = UUID.randomUUID();
        Instant runAt = Instant.now().plusSeconds(3600);

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        service.schedulePickupReminder(pickupId, runAt);

        ArgumentCaptor<JobDetail> jobCaptor = ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).scheduleJob(jobCaptor.capture(), any(Trigger.class));

        JobDetail job = jobCaptor.getValue();
        assertThat(job.getKey().getName()).isEqualTo(pickupId.toString());
        assertThat(job.getKey().getGroup()).isEqualTo(JobGroup.PICKUP_REMINDER.value());
        assertThat(job.getJobClass()).isEqualTo(PickupReminderJob.class);
    }

    @Test
    void schedulePickupExpiry_schedulesJobWithCorrectKey() throws SchedulerException {
        UUID pickupId = UUID.randomUUID();
        Instant runAt = Instant.now().plusSeconds(7200);

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        service.schedulePickupExpiry(pickupId, runAt);

        ArgumentCaptor<JobDetail> jobCaptor = ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).scheduleJob(jobCaptor.capture(), any(Trigger.class));

        JobDetail job = jobCaptor.getValue();
        assertThat(job.getKey().getGroup()).isEqualTo(JobGroup.PICKUP_EXPIRY.value());
        assertThat(job.getJobClass()).isEqualTo(PickupExpiryJob.class);
    }

    @Test
    void scheduleHarvestMaturity_existingJob_deletesBeforeRescheduling() throws SchedulerException {
        UUID applicationId = UUID.randomUUID();
        Instant runAt = Instant.now().plusSeconds(60);

        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        service.scheduleHarvestMaturity(applicationId, runAt);

        verify(scheduler).deleteJob(JobKey.jobKey(applicationId.toString(), JobGroup.HARVEST.value()));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void cancelHarvestMaturity_existingJob_deletesIt() throws SchedulerException {
        UUID applicationId = UUID.randomUUID();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        service.cancelHarvestMaturity(applicationId);

        verify(scheduler).deleteJob(JobKey.jobKey(applicationId.toString(), JobGroup.HARVEST.value()));
    }

    @Test
    void cancelPickupJobs_deletesReminderAndExpiry() throws SchedulerException {
        UUID pickupId = UUID.randomUUID();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        service.cancelPickupJobs(pickupId);

        verify(scheduler).deleteJob(JobKey.jobKey(pickupId.toString(), JobGroup.PICKUP_REMINDER.value()));
        verify(scheduler).deleteJob(JobKey.jobKey(pickupId.toString(), JobGroup.PICKUP_EXPIRY.value()));
    }
}
