package com.newton.zaocycle.collection.infrastructure.persistence;

import com.newton.zaocycle.collection.application.PickupFilter;
import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.collection.domain.port.PickupRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class WastePickupRepositoryAdapter implements PickupRepository {

    private final WastePickupJpaRepository jpa;

    WastePickupRepositoryAdapter(WastePickupJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<WastePickup> findById(UUID id) {
        return jpa.findById(id).map(WastePickupEntityMapper::toDomain);
    }

    @Override
    public WastePickup save(WastePickup pickup) {
        return WastePickupEntityMapper.toDomain(
                jpa.save(WastePickupEntityMapper.toEntity(pickup)));
    }

    @Override
    public List<WastePickup> findByRiderIdAndScheduledFor(UUID riderId, LocalDate date) {
        return jpa.findByRiderIdAndScheduledFor(riderId, date).stream()
                .map(WastePickupEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<WastePickup> findByFarmerId(UUID farmerId) {
        return jpa.findByFarmerId(farmerId).stream()
                .map(WastePickupEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByFarmerIdAndStatusIn(UUID farmerId, List<PickupStatus> statuses) {
        List<String> names = statuses.stream().map(PickupStatus::name).toList();
        return jpa.existsByFarmerIdAndStatusIn(farmerId, names);
    }

    @Override
    public BigDecimal sumPayoutsByFarmer(UUID farmerId) {
        return jpa.sumPayoutsByFarmer(farmerId);
    }

    @Override
    public BigDecimal sumPayoutsByFarmerSince(UUID farmerId, Instant since) {
        return jpa.sumPayoutsByFarmerSince(farmerId, since);
    }

    @Override
    public long countPaidByFarmer(UUID farmerId) {
        return jpa.countPaidByFarmer(farmerId);
    }

    @Override
    public List<WastePickup> findRecentPaidByFarmer(UUID farmerId, int limit) {
        return jpa.findTop3ByFarmerIdAndStatusOrderByPaidAtDesc(farmerId, PickupStatus.PAID.name()).stream()
                .map(WastePickupEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Page<WastePickup> search(PickupFilter filter, Pageable pageable) {
        Specification<WastePickupEntity> spec = buildSpec(filter);
        return jpa.findAll(spec, pageable).map(WastePickupEntityMapper::toDomain);
    }

    private Specification<WastePickupEntity> buildSpec(PickupFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status().name()));
            }
            if (filter.riderId() != null) {
                predicates.add(cb.equal(root.get("riderId"), filter.riderId()));
            }
            if (filter.farmerId() != null) {
                predicates.add(cb.equal(root.get("farmerId"), filter.farmerId()));
            }
            if (filter.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scheduledFor"), filter.fromDate()));
            }
            if (filter.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scheduledFor"), filter.toDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
