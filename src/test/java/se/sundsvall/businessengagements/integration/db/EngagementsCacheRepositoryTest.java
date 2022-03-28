package se.sundsvall.businessengagements.integration.db;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
@Transactional
class EngagementsCacheRepositoryTest {
    
    EngagementsCacheRepository repository;
    
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final String PARTY_ID = "6a5c3d04-412d-11ec-973a-0242ac130003";
    private final BusinessEngagementsResponse response = new BusinessEngagementsResponse();
    
    @BeforeEach
    public void setup() {
        EngagementsCacheEntity persistedEntity = EngagementsCacheEntity.builder()
                .withPartyId(PARTY_ID)
                .withUpdated(localDateTime)
                .withResponse(response)
                .build();
        
        //repository.persist(persistedEntity);
    }
    
    /*@Test
    void testFindByPersonId(final SoftAssertions softly) {
        
        final BvCacheEntity entity = repository.findByPersonId(PERSON_ID).get();
        softly.assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
        softly.assertThat(entity.getResponse()).isEqualTo(response);
        softly.assertThat(entity.getUpdated()).isEqualTo(localDateTime);
    }
    
    @Test
    void testRemoveByPersonId(final SoftAssertions softly) {
        final long amountRemoved = repository.removeByPersonId(PERSON_ID);
        softly.assertThat(amountRemoved).isEqualTo(1L);
    }*/
}