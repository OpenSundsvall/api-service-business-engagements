package se.sundsvall.businessengagements.integration.citizenmapping;

import se.sundsvall.businessengagements.integration.BusinessEngagementsHealthIndicator;

public class CitizenMappingHealthIndicator extends BusinessEngagementsHealthIndicator {
    
    public CitizenMappingHealthIndicator() {
        super(CitizenMappingIntegration.INTEGRATION_NAME);
    }
}
