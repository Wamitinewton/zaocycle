package com.newton.zaocycle.ussd.infrastructure.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import com.newton.zaocycle.ussd.domain.port.UssdSessionStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisUssdSessionStore implements UssdSessionStore {

    private static final String KEY_PREFIX = "ussd:session:";

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final Duration ttl;

    public RedisUssdSessionStore(StringRedisTemplate redis, ObjectMapper mapper,
            @Value("${zaocycle.ussd.session-ttl-minutes:5}") int ttlMinutes) {
        this.redis = redis;
        this.mapper = mapper;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    @Override
    public Optional<UssdSession> find(String sessionId) {
        String json = redis.opsForValue().get(KEY_PREFIX + sessionId);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(mapper.readValue(json, UssdSession.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize USSD session", e);
        }
    }

    @Override
    public void save(UssdSession session) {
        try {
            String json = mapper.writeValueAsString(session);
            redis.opsForValue().set(KEY_PREFIX + session.getSessionId(), json, ttl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize USSD session", e);
        }
    }

    @Override
    public void delete(String sessionId) {
        redis.delete(KEY_PREFIX + sessionId);
    }
}
