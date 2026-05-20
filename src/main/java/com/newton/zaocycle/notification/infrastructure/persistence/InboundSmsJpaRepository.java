package com.newton.zaocycle.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface InboundSmsJpaRepository extends JpaRepository<InboundSmsEntity, UUID> {
}
