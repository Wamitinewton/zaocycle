package com.newton.zaocycle.notification.application;

import com.newton.zaocycle.notification.domain.model.SmsTemplate;
import com.newton.zaocycle.notification.domain.port.SmsTemplateRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsTemplateServiceTest {

    @Mock
    private SmsTemplateRepository smsTemplateRepository;

    private SmsTemplateService service;

    @BeforeEach
    void setUp() {
        service = new SmsTemplateService(smsTemplateRepository);
    }

    @Test
    void warmCache_loadsAllTemplates() {
        when(smsTemplateRepository.findAll()).thenReturn(List.of(
                new SmsTemplate(UUID.randomUUID(), "HELLO", "Hello {{name}}!", "en", Instant.now())
        ));

        service.warmCache();

        String rendered = service.render("HELLO", Map.of("name", "Alice"));
        assertThat(rendered).isEqualTo("Hello Alice!");
    }

    @Test
    void render_replacesMultiplePlaceholders() {
        when(smsTemplateRepository.findAll()).thenReturn(List.of(
                new SmsTemplate(UUID.randomUUID(), "CONFIRM", "Hi {{name}}, your {{item}} is ready.", "en", Instant.now())
        ));
        service.warmCache();

        String result = service.render("CONFIRM", Map.of("name", "Bob", "item", "order"));
        assertThat(result).isEqualTo("Hi Bob, your order is ready.");
    }

    @Test
    void render_throwsNotFound_whenTemplateCodeMissing() {
        when(smsTemplateRepository.findAll()).thenReturn(List.of());
        service.warmCache();

        assertThatThrownBy(() -> service.render("NONEXISTENT", Map.of()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("NONEXISTENT");
    }
}
