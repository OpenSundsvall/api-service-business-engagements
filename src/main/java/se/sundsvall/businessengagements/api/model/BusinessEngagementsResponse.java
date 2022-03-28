package se.sundsvall.businessengagements.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEngagementsResponse implements Serializable {
    
    private List<Engagement> engagements;
    
    @Schema(description = "In case fetching one or more engagement failed, this will show why it failed."
            + " There may be more than one description if several engagements failed.",
            example = "Timeout")
    private Map<String, String> statusDescriptions;
    
    @Schema(description = "If fetching all engagements went \"OK\" or \"NOK\". "
            + "A \"NOK\" may still return engagements but indicates that the information is incomplete.",
            example = "OK", required = true)
    private Status status;
    
    public void addEngagement(Engagement engagement) {
        if(this.engagements == null) {
            this.engagements = new ArrayList<>();
        }
        this.engagements.add(engagement);
    }
    
    /**
     * Convenience method for adding an engagement to the response.
     * @param organizationName
     * @param organizationNumber
     */
    public void addEngagement(String organizationName, String organizationNumber) {
        addEngagement(Engagement.builder()
                .withOrganizationName(organizationName)
                .withOrganizationNumber(organizationNumber)
                .build());
    }
    
    /**
     * Adds a status description to the response, only used when something went wrong from integrations.
     * @param type
     * @param statusDescription
     */
    public void addStatusDescription(String type, String statusDescription) {
        if(this.statusDescriptions == null) {
            this.statusDescriptions = new HashMap<>();
        }
        this.statusDescriptions.put(type, statusDescription);
    }
    
    @Getter
    @Setter
    @Builder(setterPrefix = "with")
    @Schema(description = "Represents a persons business engagement.")
    public static class Engagement implements Serializable {
        @Schema(description = "Name of the organization", example = "Styrbjörns båtar")
        private String organizationName;
        
        @Schema(description = "Organization number, may also be personal number in case of enskild firma", example = "2021005448")
        private String organizationNumber;
    
        @Schema(description = "Unique id for the organization (UUID)", example = "bab17d8b-af38-4531-967c-083f15ca1571")
        private String organizationId;
    }
    
    @Schema()
    public enum Status implements Serializable {
        OK, NOK
    }
}
