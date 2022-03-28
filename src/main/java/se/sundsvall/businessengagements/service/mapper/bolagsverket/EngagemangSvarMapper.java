package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.springframework.stereotype.Component;
import se.bolagsverket.schema.ssbt.fel.FelTyp;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangSvar;
import se.bolagsverket.schema.ssbten.engagemang.Foretag;
import se.bolagsverket.schema.ssbten.engagemang.ForetagEngagemang;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles response mapping from Bolagsverket
 */
@Component
public class EngagemangSvarMapper {
    
    /**
     * map Bolagsverket response {@link EngagemangSvar} to {@link BusinessEngagementsResponse}
     * Since Bolagsverket has a structure that
     * @param engagemangSvar
     * @return {@link BusinessEngagementsResponse}
     */
    public BusinessEngagementsResponse mapBolagsverketResponse(EngagemangSvar engagemangSvar) {
        
        final List<Foretag> foretagList =  getSuccessfulEngagements(engagemangSvar);
        
        //Now we have all Foretag objects
        final BusinessEngagementsResponse response = mapSuccessfulEngagementsToResponse(foretagList);
        
        //But we don't have those engagements that might have failed. map those as well
        mapFailedEngagementsToResponse(engagemangSvar, response);
    
        return response;
    }
    
    /**
     * Extract all successful engagements / "Foretag" from the response.
     * @param engagemangSvar
     * @return {@link List} of {@link Foretag}
     */
    List<Foretag> getSuccessfulEngagements(final EngagemangSvar engagemangSvar) {
        //Information for each "Foretag" is in a list in the object "ForetagsEngagemang", which is also a list
        //Flatten all "Foretag".
        return engagemangSvar.getEngagemangSvarDetaljer()
                .getForetagEngagemangs()
                .stream()
                .map(ForetagEngagemang::getForetags)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    
    /**
     * Map all succesful {@link se.bolagsverket.schema.ssbten.engagemang.Foretag} from Bolagsverket
     * @param foretagList
     * @return
     */
    BusinessEngagementsResponse mapSuccessfulEngagementsToResponse(List<Foretag> foretagList) {
        BusinessEngagementsResponse response = new BusinessEngagementsResponse();
        
        foretagList.forEach(foretag -> {
            //Get the type of business, e.g. "Aktiebolag" or "Enskild firma" etc.
            final String foretagsformKod = foretag.getForetagsform().getForetagsformKod();
        
            //Check if it's an "enskild firma", in that case we handle business information differently
            if("E".equalsIgnoreCase(foretagsformKod)) {
                extractAndSetNameAndPersonalNumber(response, foretag);
            } else{
                if(foretag.getForetagId() != null && foretag.getForetagId().getPersonIdentitetsbeteckning() != null) {
                    response.addEngagement(
                            foretag.getForetagNamn(), foretag.getForetagId().getPersonIdentitetsbeteckning().getOrganisationsnummer());
                } //else: We don't want to map it.
            }
        });
        
        return response;
    }
    
    /**
     * Gets the name and personal number (org.no) in case it's an "Enskild firma".
     * @param response
     * @param foretag
     */
    void extractAndSetNameAndPersonalNumber(final BusinessEngagementsResponse response, final Foretag foretag) {
        
        //First extract the personnumber since it's the same "everywhere"
        String personalNumber = foretag.getForetagId().getPersonIdentitetsbeteckning().getPersonnummer();
        
        //If the name of the "enskild firma" is protected, it may be found in "ForetagNamn" in the "NamnSkydd" field
        if(!foretag.getNamnskydds().isEmpty()) { //Check if we have something
            //If we do, it seems like the person may have several "enskild firma", map them all to a separate engagement
            foretag.getNamnskydds().forEach(namnskydd -> response.addEngagement(namnskydd.getForetagNamn(), personalNumber));
        } else {
            response.addEngagement(foretag.getForetagNamn(), personalNumber);
        }
    }
    
    /**
     * Adds a status description to the response for each error found.
     * @param engagemangSvar
     * @param response
     */
    void mapFailedEngagementsToResponse(final EngagemangSvar engagemangSvar, final BusinessEngagementsResponse response) {
        final List<ForetagEngagemang> foretagEngagemangList = new ArrayList<>(engagemangSvar.getEngagemangSvarDetaljer()
                .getForetagEngagemangs());
        
        foretagEngagemangList.forEach(engagemang -> {
            if(engagemang.getFel() != null) {
                response.addStatusDescription(translateType(engagemang.getFel().getTyp()), engagemang.getFel().getFelBeskrivning());
            }
        });
    }
    
    /**
 *      <pre>
     *     Consolidate errors and map them.
     *
     *     EJ_BEHORIG(" EjBehorig "),
     *     EJ_KOMPLETT_SVAR(" EjKomplettSvar "),
     *     FORETAG_FINNS_EJ(" ForetagFinnsEj "),
     *     OGILTIG_BEGARAN(" OgiltigBegaran "),
     *     OGILTIGT_SVAR(" OgiltigtSvar "),
     *     OTILLGANGLIG_UPPGIFTSKALLA(" OtillgangligUppgiftskalla "),
     *     TIMEOUT(" Timeout "),
     *     TVETYDIG_BEGARAN(" TvetydigBegaran "),
     *     UPPGIFT_FINNS_EJ(" UppgiftFinnsEj "),
     *     ODEFINIERAD(" Odefinierad ");
     *  </pre>
     * @param felTyp
     * @return
     */
    String translateType(FelTyp felTyp) {
        
        String returnValue = "Unknown error.";
    
        switch (felTyp) {
            case EJ_BEHORIG -> returnValue = "Not authorized.";
            case UPPGIFT_FINNS_EJ, EJ_KOMPLETT_SVAR, FORETAG_FINNS_EJ, OGILTIGT_SVAR, OGILTIG_BEGARAN, TVETYDIG_BEGARAN -> returnValue = "Missing information.";
            case OTILLGANGLIG_UPPGIFTSKALLA -> returnValue = "No response from underlying system.";
            case TIMEOUT -> returnValue = "Timeout.";
            default -> {
            }
            //will return "unknown error".
        }
        
        return returnValue;
    }
}
