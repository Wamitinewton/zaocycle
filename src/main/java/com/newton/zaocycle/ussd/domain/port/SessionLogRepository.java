package com.newton.zaocycle.ussd.domain.port;

import com.newton.zaocycle.ussd.domain.model.SessionLog;

public interface SessionLogRepository {
    void save(SessionLog log);
}
