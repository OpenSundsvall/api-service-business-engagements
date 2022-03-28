package se.sundsvall.businessengagements.integration.bolagsverket;

import se.sundsvall.businessengagements.integration.BusinessEngagementsHealthIndicator;

public class BolagsverketHealthIndicator extends BusinessEngagementsHealthIndicator {
    
    BolagsverketHealthIndicator() {
        super(BolagsverketIntegration.INTEGRATION_NAME);
    }
}
