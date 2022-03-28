package se.sundsvall.businessengagements.integration.bolagsverket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "integration.bolagsverket")
public class BolagsverketProperties {
    
    private String apiUrl;
    private long readTimeout;
    private long connectTimeout;
    
    private String keystoreLocation;
    private String keystorePassword;
    private boolean shouldUseKeyStore;
}
