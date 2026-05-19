package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.notification.domain.model.OutboundSms;
import com.newton.zaocycle.notification.domain.port.OutboundSmsRepository;
import com.newton.zaocycle.notification.domain.port.SmsDispatchResult;
import com.newton.zaocycle.notification.domain.port.SmsGateway;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class NotificationServiceImpl implements NotificationService {

    private final SmsTemplateService templateService;
    private final SmsGateway gateway;
    private final OutboundSmsRepository outboundRepo;

    NotificationServiceImpl(SmsTemplateService templateService,
                             SmsGateway gateway,
                             OutboundSmsRepository outboundRepo) {
        this.templateService = templateService;
        this.gateway = gateway;
        this.outboundRepo = outboundRepo;
    }

    @Override
    @Async("smsExecutor")
    public void sendTemplated(String phone, String templateCode, Map<String, String> vars) {
        String body = templateService.render(templateCode, vars);
        dispatch(phone, templateCode, body);
    }

    @Override
    @Async("smsExecutor")
    public void sendRaw(String phone, String body) {
        dispatch(phone, null, body);
    }

    private void dispatch(String phone, String templateCode, String body) {
        OutboundSms sms = OutboundSms.pending(phone, templateCode, body);
        outboundRepo.save(sms);

        SmsDispatchResult result = gateway.send(phone, body);

        if (result.success()) {
            sms.markSent(result.providerMessageId());
        } else {
            sms.markFailed(result.errorMessage());
        }
        outboundRepo.save(sms);
    }
}
