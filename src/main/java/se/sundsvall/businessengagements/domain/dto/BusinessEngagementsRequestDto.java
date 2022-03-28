package se.sundsvall.businessengagements.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Convenience-DTO for sending request info around.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@Getter
@Setter
public class BusinessEngagementsRequestDto {
    
    private String personalName;
    private String personalNumber;
    private String partyId;
    private String serviceName;
}
