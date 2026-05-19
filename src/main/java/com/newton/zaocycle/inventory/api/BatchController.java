package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.inventory.api.dto.CreateBatchRequest;
import com.newton.zaocycle.inventory.application.BatchService;
import com.newton.zaocycle.inventory.application.command.CreateBatchCommand;
import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/inventory")
@PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/batches")
    @ResponseStatus(HttpStatus.CREATED)
    public BriquetteBatch create(@Valid @RequestBody CreateBatchRequest request) {
        CreateBatchCommand cmd = new CreateBatchCommand(
                request.batchNumber(), request.kgProduced(),
                request.producedAt(), request.sourceIntakeId());
        return batchService.create(cmd);
    }

    @GetMapping("/batches")
    public List<BriquetteBatch> listAll() {
        return batchService.findAll();
    }

    @GetMapping("/stock")
    public BigDecimal availableStock() {
        return batchService.availableStock();
    }
}
