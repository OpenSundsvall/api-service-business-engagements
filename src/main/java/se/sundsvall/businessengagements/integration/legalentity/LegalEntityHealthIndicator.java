package se.sundsvall.businessengagements.integration.legalentity;

import se.sundsvall.businessengagements.integration.BusinessEngagementsHealthIndicator;
import se.sundsvall.businessengagements.integration.citizenmapping.CitizenMappingIntegration;

public class LegalEntityHealthIndicator extends BusinessEngagementsHealthIndicator {
    
    public LegalEntityHealthIndicator() {
        super(LegalEntityIntegration.INTEGRATION_NAME);
    }
}
