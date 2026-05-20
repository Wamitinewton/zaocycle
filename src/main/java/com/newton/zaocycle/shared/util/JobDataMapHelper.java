package com.newton.zaocycle.shared.util;

import org.quartz.JobDataMap;

public final class JobDataMapHelper {

    private JobDataMapHelper() {
    }

    public static JobDataMap of(String k1, String v1) {
        JobDataMap m = new JobDataMap();
        m.put(k1, v1);
        return m;
    }

    public static JobDataMap of(String k1, String v1, String k2, String v2) {
        JobDataMap m = new JobDataMap();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }
}
