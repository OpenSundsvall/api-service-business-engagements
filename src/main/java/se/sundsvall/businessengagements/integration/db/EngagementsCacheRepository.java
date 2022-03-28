package se.sundsvall.businessengagements.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EngagementsCacheRepository extends JpaRepository<EngagementsCacheEntity, Long> {
    
    @Transactional
    @Modifying
    int deleteByPartyId(@Param("partyIdToRemove") String partyIdToRemove);
    
    Optional<EngagementsCacheEntity> findByPartyId(@Param("partyIdToFind") String partyIdToFind);
    
    @Transactional
    @Modifying
    int deleteAllByUpdatedBefore(LocalDateTime localDateTime);
}
