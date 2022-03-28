package se.sundsvall.businessengagements.integration.db;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;

import java.time.LocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class EntityMapperTest {
    
    @Test
    void testMapResponseToEntity(final SoftAssertions softly) {
        BusinessEngagementsResponse response = new BusinessEngagementsResponse();
        response.addEngagement("123456", "654321");
    
        final EngagementsCacheEntity cacheEntity = EntityMapper.mapResponseToEntity(response, "abc123");
        softly.assertThat(cacheEntity.getResponse()).isEqualTo(response);
        softly.assertThat(cacheEntity.getPartyId()).isEqualTo("abc123");
        //since it's initialized with LocalDateTime.now() it's kind of hard to test, make sure it's somewhat correct.
        softly.assertThat(cacheEntity.getUpdated()).isAfter(LocalDateTime.now().minusSeconds(10));
        softly.assertThat(cacheEntity.getUpdated()).isBefore(LocalDateTime.now().plusSeconds(10));
        
    }
    
}