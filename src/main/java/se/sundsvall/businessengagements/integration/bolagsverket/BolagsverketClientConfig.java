package se.sundsvall.businessengagements.integration.bolagsverket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.zalando.logbook.Logbook;
import se.sundsvall.dept44.configuration.webservicetemplate.WebServiceTemplateBuilder;

import java.time.Duration;

@Configuration
public class BolagsverketClientConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(BolagsverketClientConfig.class);
    
    private final Logbook logbook;
    private final BolagsverketProperties properties;
    
    @Autowired
    public BolagsverketClientConfig(final Logbook logbook, final BolagsverketProperties properties) {
        this.logbook = logbook;
        this.properties = properties;
    }
    
    @Bean(name = "bolagsverket-ssbten-webservice-template")
    public WebServiceTemplate getSsbtenWebserviceTemplate() {
    
        final WebServiceTemplateBuilder builder = new WebServiceTemplateBuilder().withBaseUrl(properties.getApiUrl())
                .withPackageToScan("se.bolagsverket.schema.ssbten")
                .withReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
                .withConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
                .withLogbook(logbook);
        
        if(properties.isShouldUseKeyStore()) {
            LOG.info("Using keystore for Bolagsverket: {}", properties.getKeystoreLocation());
            builder.withKeyStoreFileLocation(properties.getKeystoreLocation());
            builder.withKeyStorePassword(properties.getKeystorePassword());
        } else {
            LOG.info("Not using any keystore for Bolagsverket");
        }
        
        return builder.build();
    }
}
