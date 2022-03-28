package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import se.bolagsverket.schema.ssbt.metadata.Datakonsument;
import se.bolagsverket.schema.ssbt.metadata.ObjectFactory;
import se.bolagsverket.schema.ssbt.metadata.PartId;
import se.bolagsverket.schema.ssbt.metadata.Service;

import org.springframework.stereotype.Component;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;

/**
 * <pre>
 *     Skapar följande xml:
 *     {@code
 *       <md:Datakonsument>
 *           <md:PartId>
 *               <md:Organisationsnummer>2021001234</md:Organisationsnummer>
 *           </md:PartId>
 *           <md:PartNamn>SundsvallsKommun</md:PartNamn>
 *           <md:Service>
 *               <md:ServiceNamn>BUSENGBV</md:ServiceNamn>
 *           </md:Service>
 *       </md:Datakonsument>
 *     }
 *
 * </pre>
 */
@Component
class DatakonsumentMapper {
    
    private static final String SUNDSVALL_MUNICIPALITY_ORGANIZATION_NUMBER = "2120002411";
    private static final String SERVICE_NAME = "BUSENGBV";  //This is communicated between Bolagsverket and Sundsvalls kommun, must be set in each request.
    
    ObjectFactory metaDataFactory = new ObjectFactory();
    
    /**
     * <pre>
     *     From bolagsverkets documentation:
     *     "SSBTEN kontrollerar
     *      att datakonsumentens organisationsnummer i begäran överensstämmer med
     *      organisationsnumret i datakonsumentens organisationscertifikat samt att
     *      organisationsnummer och servicenamn i begäran finns registrerad hos Bolagsverket"
     *
     * i.e make sure that all names correspond whith those registered at Bolagsverket
     *  - PartId: e.g. SundsvallsKommun?
     *  - ServiceNamn: BUSENGBV
     *  - Organisationsnummer: kommunens orgnummer?
     *
     *  </pre>
     * @return {}
     * @param requestDto
     */
    Datakonsument createDatakonsument(final BusinessEngagementsRequestDto requestDto) {
        return metaDataFactory.createDatakonsument()
                .withPartId(createPartId(requestDto))
                .withPartNamn("SundsvallsKommun")  //I think we can use whatever here.
                .withService(createService());
    }
    
    private PartId createPartId(final BusinessEngagementsRequestDto requestDto) {
        return metaDataFactory.createPartId()
                .withOrganisationsnummer(SUNDSVALL_MUNICIPALITY_ORGANIZATION_NUMBER);
    }
    
    private Service createService() {
        return metaDataFactory.createService()
                .withServiceNamn(SERVICE_NAME);
    }
}
