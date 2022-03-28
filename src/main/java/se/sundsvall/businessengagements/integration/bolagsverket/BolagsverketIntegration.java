package se.sundsvall.businessengagements.integration.bolagsverket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.zalando.problem.Problem;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangSvar;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Component
public class BolagsverketIntegration extends BolagsverketHealthIndicator {
    
    private static final Logger LOG = LoggerFactory.getLogger(BolagsverketIntegration.class);
    public static final String INTEGRATION_NAME = "Bolagsverket";
    
    private final WebServiceTemplate webServiceTemplate;
    
    public BolagsverketIntegration(
            @Qualifier("bolagsverket-ssbten-webservice-template") final WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }
    
    public EngagemangSvar callBolagsverket(EngagemangBegaran engagemangBegaranRequest) {
        try {
            final EngagemangSvar foretagsengagemang = (EngagemangSvar) webServiceTemplate.marshalSendAndReceive(engagemangBegaranRequest);
            setUpStatus();
            
            return foretagsengagemang;
        } catch (Exception e) {
            setOutOfService(e);
            LOG.error("Someting wong", e);
            throw Problem.builder()
                    .withTitle("Error while getting digital engagements from bolagsverket")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .withDetail(e.getMessage())
                    .build();
            
        }
    }
}
