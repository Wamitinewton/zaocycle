package com.newton.zaocycle.scheduling.infrastructure.quartz;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
class QuartzConfig implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SchedulerFactoryBean schedulerFactory) {
            schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
            schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        }
        return bean;
    }
}
