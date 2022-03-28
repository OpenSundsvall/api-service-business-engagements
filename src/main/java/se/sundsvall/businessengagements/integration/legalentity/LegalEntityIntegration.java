package se.sundsvall.businessengagements.integration.legalentity;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class LegalEntityIntegration extends LegalEntityHealthIndicator {
    
    private static final Logger LOG = LoggerFactory.getLogger(LegalEntityIntegration.class);
    public static final String INTEGRATION_NAME = "legalentity";
    private static final String GUID_URI = "/guid";
    
    private final WebClient webClient;
    
    @Autowired
    public LegalEntityIntegration(@Qualifier("integration.legalentity.webclient") WebClient webClient) {
        this.webClient = webClient;
    }
    
    /**
     * Fetch a uuid for a organizationNumber.
     * @param organizationNumber
     * @return String containing uuid/guid
     */
    public String getGuidForOrganization(String organizationNumber) {
        LOG.info("Fetching guid for organization number: {}", organizationNumber);
    
        final Mono<String> stringMono = webClient.get()
                .uri(organizationNumber + GUID_URI)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> setUpStatus())
                .doOnError(throwable -> onError(throwable, organizationNumber)) //Print an error
                .onErrorResume((throwable -> Mono.empty()));    //We don't "care" since we don't want to fail the whole request.
    
        String responseString = stringMono.block();
    
        //For some reason I think "bodyToMono(String.class)" makes a string out of the response and adds extra "" to the string. remove it.
        if(StringUtils.isNotBlank(responseString)) {
            responseString = responseString.replace("\"", "");
        }
        
        return responseString;
    }
    
    private void onError(final Throwable error, String organizationNumber) {
        LOG.error("Couldn't fetch guid from LegalEntity for organizationNumber: {}", organizationNumber, error);
        setOutOfServiceWithMessage("Couldn't fetch guid for organizationNumber: " + organizationNumber);
    }
}
