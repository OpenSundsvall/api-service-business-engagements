package se.sundsvall.businessengagements.integration.citizenmapping;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.citizenmapping")
public class CitizenMappingProperties {
    
    private String apiUrl;
    private String oauth2TokenUrl;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
    private String connectTimeout;
    private String readTimeout;
}
