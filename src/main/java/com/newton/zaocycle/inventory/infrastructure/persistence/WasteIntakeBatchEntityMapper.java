package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;

import java.util.List;
import java.util.UUID;

final class WasteIntakeBatchEntityMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private WasteIntakeBatchEntityMapper() {}

    static WasteIntakeBatch toDomain(WasteIntakeBatchEntity e) {
        return new WasteIntakeBatch(
                e.getId(), e.getIntakeDate(), e.getTotalKg(),
                parsePickupIds(e.getPickupIds()),
                e.getNotes(), e.getRecordedBy(), e.getCreatedAt()
        );
    }

    static WasteIntakeBatchEntity toEntity(WasteIntakeBatch domain) {
        WasteIntakeBatchEntity e = new WasteIntakeBatchEntity();
        e.setId(domain.id());
        e.setIntakeDate(domain.intakeDate());
        e.setTotalKg(domain.totalKg());
        e.setPickupIds(serializePickupIds(domain.pickupIds()));
        e.setNotes(domain.notes());
        e.setRecordedBy(domain.recordedBy());
        return e;
    }

    private static List<UUID> parsePickupIds(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse pickup_ids JSON: " + json, e);
        }
    }

    private static String serializePickupIds(List<UUID> ids) {
        try {
            return MAPPER.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize pickup_ids", e);
        }
    }
}
