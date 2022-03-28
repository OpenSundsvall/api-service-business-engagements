package se.sundsvall.businessengagements.integration.citizenmapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import se.sundsvall.dept44.configuration.webclient.WebClientBuilder;

import java.time.Duration;

@Configuration
public class CitizenMappingConfig {
    
    private final CitizenMappingProperties properties;
    private final Logbook logbook;
    
    @Autowired
    CitizenMappingConfig(final CitizenMappingProperties properties, Logbook logbook) {
        this.properties = properties;
        this.logbook = logbook;
    }
    
    @Bean("integration.citizenmapping.webclient")
    public WebClient webClient() {
        return new WebClientBuilder()
                .withOAuth2Client(clientRegistration())
                .withConnectTimeout(Duration.ofMillis(5000))
                .withReadTimeout(Duration.ofMillis(10000))
                .withBaseUrl(properties.getApiUrl())
                .withLogbook(logbook)
                .build();
    }
    
    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId("citizenmapping")
                .tokenUri(properties.getOauth2TokenUrl())
                .clientId(properties.getOauth2ClientId())
                .clientSecret(properties.getOauth2ClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }
}
