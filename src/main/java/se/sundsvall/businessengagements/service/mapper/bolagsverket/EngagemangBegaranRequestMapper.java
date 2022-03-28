package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.bolagsverket.schema.ssbt.metadata.Anvandningsomrade;
import se.bolagsverket.schema.ssbt.metadata.TTL;
import se.bolagsverket.schema.ssbt.metadata.Tidsenhet;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaranMetadata;
import se.bolagsverket.schema.ssbten.engagemang.ObjectFactory;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;
import se.sundsvall.dept44.requestid.RequestId;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Handles request mapping towards Bolagsverket.
 * To avoid getting a huge class some parts of the request has been separated into their own classes.
 *     Should generate an xml as follows:
 * <pre>
 *     {@code
 *          &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot; ?&gt;
 * &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot;&gt;
 * 	&lt;SOAP-ENV:Header/&gt;
 * 	&lt;SOAP-ENV:Body&gt;
 * 		&lt;ns3:EngagemangBegaran SchemaVersion=&quot;1.4.0&quot; xmlns:ns2=&quot;http://schema.bolagsverket.se/ssbt/metadata&quot; xmlns:ns3=&quot;http://schema.bolagsverket.se/ssbten/engagemang&quot; xmlns:ns4=&quot;http://schema.bolagsverket.se/ssbt/foretag&quot; xmlns:ns5=&quot;http://schema.bolagsverket.se/ssbt/fel&quot; xmlns:ns6=&quot;http://schema.bolagsverket.se/ssbten/servicefel&quot;&gt;
 * 			&lt;ns3:EngagemangBegaranMetadata&gt;
 * 				&lt;ns2:MeddelandeId&gt;444e9a8e-02d7-4f28-aa1d-0d3a026ad414&lt;/ns2:MeddelandeId&gt;
 * 				&lt;ns2:TransaktionId&gt;a52c4b4b-1c61-4931-ad3c-76f7441e771e&lt;/ns2:TransaktionId&gt;
 * 				&lt;ns2:Tidstampel&gt;2022-02-17T07:23:21.156Z&lt;/ns2:Tidstampel&gt;
 * 				&lt;ns2:TTL Tidsenhet=&quot;Millisekunder&quot;&gt;15000&lt;/ns2:TTL&gt;
 * 				&lt;ns2:Datakonsument&gt;
 * 					&lt;ns2:PartId&gt;
 * 						&lt;!--ns2:Personnummer&gt;198006140159&lt;/ns2:Personnummer--&gt;
 * 						&lt;ns2:Organisationsnummer&gt;2120002411&lt;/ns2:Organisationsnummer&gt;
 * 					&lt;/ns2:PartId&gt;
 * 					&lt;ns2:PartNamn&gt;SundsvallsKommun&lt;/ns2:PartNamn&gt;
 * 					&lt;ns2:Service&gt;
 * 						&lt;ns2:ServiceNamn&gt;BUSENGBV&lt;/ns2:ServiceNamn&gt;
 * 					&lt;/ns2:Service&gt;
 * 				&lt;/ns2:Datakonsument&gt;
 * 				&lt;ns2:Anvandare&gt;
 * 					&lt;ns2:PartId&gt;
 * 						&lt;ns2:Organisationsnummer&gt;2120002411&lt;/ns2:Organisationsnummer&gt;
 * 						&lt;!--ns2:Personnummer&gt;198006140159&lt;/ns2:Personnummer--&gt;
 * 					&lt;/ns2:PartId&gt;
 * 					&lt;ns2:PartNamn&gt;Martin Hansson&lt;/ns2:PartNamn&gt;
 * 				&lt;/ns2:Anvandare&gt;
 * 				&lt;ns2:Anvandningsomrade&gt;DirektAteranvandning&lt;/ns2:Anvandningsomrade&gt;
 * 			&lt;/ns3:EngagemangBegaranMetadata&gt;
 * 			&lt;ns3:EngagemangBegaranDetaljer&gt;
 * 				&lt;ns3:PersonId&gt;
 * 					&lt;ns4:PersonIdentitetsbeteckning&gt;
 * 						&lt;ns4:Personnummer&gt;198006140159&lt;/ns4:Personnummer&gt;
 * 					&lt;/ns4:PersonIdentitetsbeteckning&gt;
 * 				&lt;/ns3:PersonId&gt;
 * 				&lt;ns3:Foretagsformer allaEnskilda=&quot;true&quot;&gt;
 * 					&lt;ns4:ForetagsformKod&gt;AB&lt;/ns4:ForetagsformKod&gt;
 * 					&lt;ns4:ForetagsformKod&gt;BRF&lt;/ns4:ForetagsformKod&gt;
 * 					&lt;ns4:ForetagsformKod&gt;E&lt;/ns4:ForetagsformKod&gt;
 * 					&lt;ns4:ForetagsformKod&gt;EK&lt;/ns4:ForetagsformKod&gt;
 * 					&lt;ns4:ForetagsformKod&gt;HB&lt;/ns4:ForetagsformKod&gt;
 * 					&lt;ns4:ForetagsformKod&gt;KB&lt;/ns4:ForetagsformKod&gt;
 * 				&lt;/ns3:Foretagsformer&gt;
 * 			&lt;/ns3:EngagemangBegaranDetaljer&gt;
 * 		&lt;/ns3:EngagemangBegaran&gt;
 * 	&lt;/SOAP-ENV:Body&gt;
 * &lt;/SOAP-ENV:Envelope&gt;
 *     }
 *
 * </pre>
 */
@Component
public class EngagemangBegaranRequestMapper {
    
    private static final Logger LOG = LoggerFactory.getLogger(EngagemangBegaranRequestMapper.class);
    
    /**
     * Need to set this in the request towards Bolagsverket, hence why it's a separate property.
     * @return
     */
    @Value("${integration.bolagsverket.ttl}")
    private int bolagsverketTtl;
    
    private final ObjectFactory ssbtenFactory;
    private final DatakonsumentMapper datakonsumentMapper;
    private final EngagemangBegaranDetaljerMapper engagemangBegaranDetaljerMapper;
    private final AnvandareMapper anvandareMapper;
    
    public EngagemangBegaranRequestMapper(final DatakonsumentMapper datakonsumentMapper, final EngagemangBegaranDetaljerMapper engagemangBegaranDetaljerMapper,
            final AnvandareMapper anvandareMapper) {
        this.ssbtenFactory = new ObjectFactory();
        this.datakonsumentMapper = datakonsumentMapper;
        this.engagemangBegaranDetaljerMapper = engagemangBegaranDetaljerMapper;
        this.anvandareMapper = anvandareMapper;
    }
    
    /**
     * <pre> Root-objekt in the requestet </pre>
     * @param requestDto to be mapped to a request
     * @return {@link se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran}.
     */
    public EngagemangBegaran createEngagemangBegaranRequest(final BusinessEngagementsRequestDto requestDto) {
        EngagemangBegaran engagemangBegaranRequest = ssbtenFactory.createEngagemangBegaran();
        engagemangBegaranRequest.setEngagemangBegaranMetadata(createEngagemangBegaranMetadata(requestDto));
        engagemangBegaranRequest.setEngagemangBegaranDetaljer(engagemangBegaranDetaljerMapper.createEngagemangBegaranDetaljer(requestDto));
        engagemangBegaranRequest.setSchemaVersion("1.4.0"); //TODO from properties?
        
        return engagemangBegaranRequest;
    }
    
    /**
     * <pre>
     * Populates {@link EngagemangBegaranMetadata}.
     * Must contain:
     *  - {@link se.bolagsverket.schema.ssbt.metadata.Datakonsument}
     *  - {@link se.bolagsverket.schema.ssbt.metadata.ServicePart} Formedlare (its own object, because of reasons...)
     *  - {@link se.bolagsverket.schema.ssbt.metadata.Anvandningsomrade}
     *  </pre>
     * @param requestDto
     * @return
     */
    EngagemangBegaranMetadata createEngagemangBegaranMetadata(final BusinessEngagementsRequestDto requestDto) {
        final EngagemangBegaranMetadata engagemangBegaranMetadata = ssbtenFactory.createEngagemangBegaranMetadata();
        engagemangBegaranMetadata.setMeddelandeId(UUID.randomUUID().toString());    //Unique for the transaction I guess?
        engagemangBegaranMetadata.setTransaktionId(RequestId.get());                //Unique for the request, should be taken from x-request-id
        engagemangBegaranMetadata.setTidstampel(getXMLGregorianCalendarTimeStamp());
        engagemangBegaranMetadata.setTTL(createTtl());
        
        engagemangBegaranMetadata.setDatakonsument(datakonsumentMapper.createDatakonsument(requestDto));
        
        engagemangBegaranMetadata.setAnvandare(anvandareMapper.createAnvandare(requestDto));
        
        engagemangBegaranMetadata.setAnvandningsomrade(Anvandningsomrade.INDIREKT_ATERANVANDNING);  //Since we're a municipality, only indirect is allowed
        
        return engagemangBegaranMetadata;
    }
    
    TTL createTtl() {
        se.bolagsverket.schema.ssbt.metadata.ObjectFactory metadataFactory = new se.bolagsverket.schema.ssbt.metadata.ObjectFactory();
        final TTL ttl = metadataFactory.createTTL();
        ttl.setTidsenhet(Tidsenhet.MILLISEKUNDER);
        
        ttl.setValue(this.bolagsverketTtl);
        
        return ttl;
    }
    
    /**
     * <pre>
     *     BV requires that "tidstampel" is sent on the format: "2016-04-27T12:29:54.856Z"
     * </pre>
     * @return
     */
    XMLGregorianCalendar getXMLGregorianCalendarTimeStamp() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC)));
        } catch (DatatypeConfigurationException e) {
            //Should never happen?
            LOG.error("Couldn't create XMLGregorianCalendar.");
        }
        
        return null;    //Should also never happen?
    }
}
