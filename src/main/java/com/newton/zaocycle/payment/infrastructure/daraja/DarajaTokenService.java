package com.newton.zaocycle.payment.infrastructure.daraja;

import com.newton.zaocycle.payment.infrastructure.daraja.dto.DarajaTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class DarajaTokenService {

    private static final Logger log = LoggerFactory.getLogger(DarajaTokenService.class);
    private static final String CACHE_KEY = "daraja:token";

    private final DarajaProperties props;
    private final StringRedisTemplate redis;
    private final RestClient restClient;

    public DarajaTokenService(DarajaProperties props,
                               StringRedisTemplate redis,
                               RestClient.Builder restClientBuilder) {
        this.props = props;
        this.redis = redis;
        this.restClient = restClientBuilder.build();
    }

    public String getToken() {
        String cached = redis.opsForValue().get(CACHE_KEY);
        return cached != null ? cached : refresh();
    }

    private synchronized String refresh() {
        String cached = redis.opsForValue().get(CACHE_KEY);
        if (cached != null) return cached;

        String credentials = Base64.getEncoder().encodeToString(
                (props.consumerKey() + ":" + props.consumerSecret()).getBytes(StandardCharsets.UTF_8));

        DarajaTokenResponse response = restClient.get()
                .uri(props.baseUrl() + "/oauth/v1/generate?grant_type=client_credentials")
                .header("Authorization", "Basic " + credentials)
                .retrieve()
                .body(DarajaTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("Daraja returned null token response");
        }

        Duration ttl = Duration.ofSeconds(response.expiresIn()).minus(Duration.ofMinutes(5));
        redis.opsForValue().set(CACHE_KEY, response.accessToken(), ttl);
        log.debug("Daraja token refreshed, expires in {}s", response.expiresIn());
        return response.accessToken();
    }
}
