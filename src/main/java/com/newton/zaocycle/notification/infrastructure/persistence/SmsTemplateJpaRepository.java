package com.newton.zaocycle.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SmsTemplateJpaRepository extends JpaRepository<SmsTemplateEntity, UUID> {
    Optional<SmsTemplateEntity> findByCode(String code);
}
