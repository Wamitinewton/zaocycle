package com.newton.zaocycle.scheduling.application.job;

import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.application.event.ApplicationMatured;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.UUID;

public class HarvestMaturityJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        UUID applicationId = UUID.fromString(context.getMergedJobDataMap().getString("applicationId"));

        ApplicationContext appCtx = JobContextUtil.applicationContext(context);
        PesticideApplicationService service = appCtx.getBean(PesticideApplicationService.class);

        PesticideApplication application = service.markSafe(applicationId);

        appCtx.publishEvent(new ApplicationMatured(
                application.id(), application.farmerId(), application.crop(),
                application.chemicalId(), application.appliedAt(), application.safeHarvestDate()
        ));
    }
}
