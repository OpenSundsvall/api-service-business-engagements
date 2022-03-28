package se.sundsvall.businessengagements.integration.legalentity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.legalentity")
public class LegalEntityProperties {
    
    private String apiUrl;
    private String oauth2TokenUrl;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
    private String connectTimeout;
    private String readTimeout;
}
