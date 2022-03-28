package se.sundsvall.businessengagements.integration.db;

import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;

import java.time.LocalDateTime;

public class EntityMapper {
    
    private EntityMapper() { }
    
    public static EngagementsCacheEntity mapResponseToEntity(BusinessEngagementsResponse response, String partyId) {
        return EngagementsCacheEntity.builder()
                .withPartyId(partyId)
                .withResponse(response)
                .withUpdated(LocalDateTime.now())
                .build();
    }
}
