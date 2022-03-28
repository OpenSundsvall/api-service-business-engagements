package se.sundsvall.businessengagements.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangSvar;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;
import se.sundsvall.businessengagements.integration.bolagsverket.BolagsverketIntegration;
import se.sundsvall.businessengagements.integration.citizenmapping.CitizenMappingIntegration;
import se.sundsvall.businessengagements.integration.db.EngagementsCacheRepository;
import se.sundsvall.businessengagements.integration.db.EntityMapper;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;
import se.sundsvall.businessengagements.integration.legalentity.LegalEntityIntegration;
import se.sundsvall.businessengagements.service.mapper.bolagsverket.EngagemangBegaranRequestMapper;
import se.sundsvall.businessengagements.service.mapper.bolagsverket.EngagemangSvarMapper;

import java.util.Optional;

@Service
@EnableTransactionManagement
public class BusinessEngagementsService {
    
    private static final Logger LOG = LoggerFactory.getLogger(BusinessEngagementsService.class);
    
    private final EngagemangBegaranRequestMapper engagemangBegaranRequestMapper;
    private final BolagsverketIntegration bolagsverketIntegration;
    private final EngagemangSvarMapper engagemangSvarMapper;
    private final CitizenMappingIntegration citizenMappingIntegration;
    private final EngagementsCacheRepository engagementsCacheRepository;
    private final LegalEntityIntegration legalEntityIntegration;
    
    public BusinessEngagementsService(final EngagemangBegaranRequestMapper engagemangBegaranRequestMapper, final BolagsverketIntegration bolagsverketIntegration, final EngagemangSvarMapper engagemangSvarMapper,
            final CitizenMappingIntegration citizenMappingIntegration, final EngagementsCacheRepository engagementsCacheRepository, final LegalEntityIntegration legalEntityIntegration) {
        this.engagemangBegaranRequestMapper = engagemangBegaranRequestMapper;
        this.bolagsverketIntegration = bolagsverketIntegration;
        this.engagemangSvarMapper = engagemangSvarMapper;
        this.citizenMappingIntegration = citizenMappingIntegration;
        this.engagementsCacheRepository = engagementsCacheRepository;
        this.legalEntityIntegration = legalEntityIntegration;
    }
    
    /**
     * Handles fetching of engagements, storing to database cache and updating the cache.
     * @param requestDto
     * @return
     */
    public BusinessEngagementsResponse getBusinessEngagements(BusinessEngagementsRequestDto requestDto) {
    
        //First check if we have a cached entity for the given partyId
        final Optional<BusinessEngagementsResponse> optionalCachedResponse = getResponseIfCached(requestDto.getPartyId());
        
        if(optionalCachedResponse.isPresent()) {
            return optionalCachedResponse.get();
        } else {
    
            requestDto.setPersonalNumber(citizenMappingIntegration.getCitizenMapping(requestDto.getPartyId()));
    
            //Create request object for Bolagsverket
            final EngagemangBegaran engagemangBegaranRequest = engagemangBegaranRequestMapper.createEngagemangBegaranRequest(requestDto);
    
            //Get the response
            final EngagemangSvar engagemangSvar = bolagsverketIntegration.callBolagsverket(engagemangBegaranRequest);
    
            //Map it to our response
            BusinessEngagementsResponse businessEngagementsResponse = engagemangSvarMapper.mapBolagsverketResponse(engagemangSvar);
            
            if(businessEngagementsResponse.getEngagements() != null && !businessEngagementsResponse.getEngagements().isEmpty()) {
                fetchAndPopulateGuidForOrganizations(businessEngagementsResponse);
                //Here we have an entity that (maybe) needs caching
                handleNewCacheEntity(businessEngagementsResponse, requestDto.getPartyId());
            }
            
    
            return businessEngagementsResponse;
        }
    }
    
    void fetchAndPopulateGuidForOrganizations(BusinessEngagementsResponse businessEngagementsResponse) {
        LOG.info("Starting to fetch guid/uuid for all organizations.");
        businessEngagementsResponse.getEngagements()
            .forEach(engagement -> {
                final String guidForOrganization = legalEntityIntegration.getGuidForOrganization(engagement.getOrganizationNumber());
                if(StringUtils.isNotBlank(guidForOrganization)) {
                    //If we have a guid, set it.
                    engagement.setOrganizationId(guidForOrganization);
                } else {
                    //If no guid, indicate this with a new status description for each.
                    businessEngagementsResponse.addStatusDescription(engagement.getOrganizationNumber(), "Couldn't fetch guid for organization number");
                }
            });
    }
    
    /**
     * Fetches a cached response
     * @param partyId
     * @return
     */
    @Transactional
    Optional<BusinessEngagementsResponse> getResponseIfCached(String partyId) {
        final Optional<EngagementsCacheEntity> possibleCachedEntity = engagementsCacheRepository.findByPartyId(partyId);
        
        Optional<BusinessEngagementsResponse> possibleResponse = Optional.empty();
        
        if(possibleCachedEntity.isPresent()) {
            final EngagementsCacheEntity entity = possibleCachedEntity.get();
            possibleResponse = Optional.of(entity.getResponse());
        }
        
        return possibleResponse;
    }
    
    @Transactional
    void handleNewCacheEntity(BusinessEngagementsResponse response, String partyId) {
        
        //We don't want to persist anything with errors.
        if(response.getStatusDescriptions() == null || response.getStatusDescriptions().isEmpty()) {
            //First we need to remove any possible old entity.
            int entitiesDeleted = engagementsCacheRepository.deleteByPartyId(partyId);
    
            if(entitiesDeleted > 0) {    //Only print if we actually remove anything
                LOG.info("Removed cached entity");
            }
    
            engagementsCacheRepository.save(EntityMapper.mapResponseToEntity(response, partyId));
            LOG.info("Persisted a new entity");
        } else {
            LOG.info("Not persisting response entity since it has missing information.");
        }
    }
}
