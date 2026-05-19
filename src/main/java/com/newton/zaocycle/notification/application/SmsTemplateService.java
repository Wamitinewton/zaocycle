package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.notification.domain.port.SmsTemplateRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsTemplateService {

    private final SmsTemplateRepository smsTemplateRepository;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public SmsTemplateService(SmsTemplateRepository smsTemplateRepository) {
        this.smsTemplateRepository = smsTemplateRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmCache() {
        smsTemplateRepository.findAll().forEach(t -> cache.put(t.code(), t.body()));
    }

    public String render(String code, Map<String, String> vars) {
        String template = cache.get(code);
        if (template == null) {
            throw new NotFoundException("SMS template not found: " + code);
        }
        String result = template;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
