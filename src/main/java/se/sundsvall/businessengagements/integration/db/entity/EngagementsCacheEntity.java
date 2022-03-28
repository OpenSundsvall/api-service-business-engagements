package se.sundsvall.businessengagements.integration.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class EngagementsCacheEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "party_id", nullable = false, unique = true)
    private String partyId;
    
    @Column(name = "last_updated")
    private LocalDateTime updated;
    
    @Lob
    @Column(name = "response", nullable = false, length = 10000)
    private BusinessEngagementsResponse response;
}
