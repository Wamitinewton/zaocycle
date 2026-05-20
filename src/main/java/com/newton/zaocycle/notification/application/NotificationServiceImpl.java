package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.notification.domain.model.OutboundSms;
import com.newton.zaocycle.notification.domain.port.OutboundSmsRepository;
import com.newton.zaocycle.notification.domain.port.SmsDispatchResult;
import com.newton.zaocycle.notification.domain.port.SmsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

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
        log.debug("sendTemplated: phone={} template={}", phone, templateCode);
        String body = templateService.render(templateCode, vars);
        dispatch(phone, templateCode, body);
    }

    @Override
    @Async("smsExecutor")
    public void sendRaw(String phone, String body) {
        log.debug("sendRaw: phone={}", phone);
        dispatch(phone, null, body);
    }

    private void dispatch(String phone, String templateCode, String body) {
        OutboundSms sms = OutboundSms.pending(phone, templateCode, body);
        outboundRepo.save(sms);

        try {
            SmsDispatchResult result = gateway.send(phone, body);
            if (result.success()) {
                sms.markSent(result.providerMessageId());
                log.info("SMS sent: phone={} template={} msgId={}", phone, templateCode, result.providerMessageId());
            } else {
                sms.markFailed(result.errorMessage());
                log.warn("SMS rejected by gateway: phone={} template={} reason={}", phone, templateCode, result.errorMessage());
            }
        } catch (Exception ex) {
            sms.markFailed(ex.getMessage());
            log.error("SMS dispatch exception: phone={} template={}", phone, templateCode, ex);
        }
        outboundRepo.save(sms);
    }
}
