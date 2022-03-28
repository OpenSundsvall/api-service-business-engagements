package se.sundsvall.businessengagements.integration.citizenmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class CitizenMappingIntegration extends CitizenMappingHealthIndicator {
    
    private static final Logger LOG = LoggerFactory.getLogger(CitizenMappingIntegration.class);
    public static final String INTEGRATION_NAME = "citizenmapping";
    private static final String PERSONAL_NUMBER_URI = "/personalnumber";
    
    private final WebClient webClient;
    
    @Autowired
    public CitizenMappingIntegration(@Qualifier("integration.citizenmapping.webclient") WebClient webClient) {
        this.webClient = webClient;
    }
    
    /**
     * Fetch personalNumber for a partyId.
     * @param partyId
     * @return
     */
    public String getCitizenMapping(String partyId) {
        LOG.info("Fetching personalNumber for partyId: {}", partyId);
    
        //TODO, write exceptionhandling.
        return webClient
                .get()
                .uri(partyId + PERSONAL_NUMBER_URI)
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, this::handleServerError)
                .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
                .bodyToMono(String.class)
                .doOnSuccess(response -> setUpStatus())
                .doOnError(error -> LOG.error("Couldn't fetch personalNumber from CitizenMapping", error))
                .block();   //We need the result before we can do anything else.
    }
    
    Mono<WebClientResponseException> handleServerError(ClientResponse response) {
        setOutOfServiceWithMessage("Couldn't fetch personalNumber from CitizenMapping");
        return response.createException();
    }
    
    Mono<WebClientResponseException> handleClientError(ClientResponse response) {
        setOutOfServiceWithMessage("Couldn't fetch personalNumber from CitizenMapping");
        return response.createException();
    }
}
