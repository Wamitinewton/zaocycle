package com.newton.zaocycle.scheduling.api;

import com.newton.zaocycle.scheduling.application.SchedulingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/scheduler")
@PreAuthorize("hasRole('ADMIN')")
public class SchedulerAdminController {

    private final SchedulingService scheduling;

    public SchedulerAdminController(SchedulingService scheduling) {
        this.scheduling = scheduling;
    }

    @PostMapping("/fast-forward/{applicationId}")
    public void fastForward(@PathVariable UUID applicationId,
                            @RequestParam(defaultValue = "30") int seconds) {
        scheduling.scheduleHarvestMaturity(applicationId, Instant.now().plusSeconds(seconds));
    }
}
