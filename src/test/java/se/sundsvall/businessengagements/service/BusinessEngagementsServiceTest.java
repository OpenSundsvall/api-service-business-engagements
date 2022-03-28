package se.sundsvall.businessengagements.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangSvar;
import se.sundsvall.businessengagements.TestObjectFactory;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;
import se.sundsvall.businessengagements.integration.bolagsverket.BolagsverketIntegration;
import se.sundsvall.businessengagements.integration.citizenmapping.CitizenMappingIntegration;
import se.sundsvall.businessengagements.integration.db.EngagementsCacheRepository;
import se.sundsvall.businessengagements.integration.db.entity.EngagementsCacheEntity;
import se.sundsvall.businessengagements.integration.legalentity.LegalEntityIntegration;
import se.sundsvall.businessengagements.service.mapper.bolagsverket.EngagemangBegaranRequestMapper;
import se.sundsvall.businessengagements.service.mapper.bolagsverket.EngagemangSvarMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SoftAssertionsExtension.class, MockitoExtension.class })
@MockitoSettings(strictness = Strictness.LENIENT)
class BusinessEngagementsServiceTest {
    
    @Mock
    private EngagemangBegaranRequestMapper mockEngagemangBegaranRequestMapper;
    
    @Mock
    private BolagsverketIntegration mockBolagsverketIntegration;
    
    @Mock
    private EngagemangSvarMapper mockEngagemangSvarMapper;
    
    @Mock
    private CitizenMappingIntegration mockCitizenMappingIntegration;
    
    @Mock
    private EngagementsCacheRepository mockRepository;
    
    @Mock
    private LegalEntityIntegration mockLegalEntityIntegration;
    
    private BusinessEngagementsService service;
    
    @BeforeEach
    public void setup() {
        service = new BusinessEngagementsService(mockEngagemangBegaranRequestMapper, mockBolagsverketIntegration, mockEngagemangSvarMapper, mockCitizenMappingIntegration, mockRepository,
                mockLegalEntityIntegration);
        when(mockLegalEntityIntegration.getGuidForOrganization(anyString())).thenReturn(UUID.randomUUID().toString());
    }
    
    @Test
    void testGetBusinessEngagements_happyPath(final SoftAssertions softly) {
        when(mockCitizenMappingIntegration.getCitizenMapping(anyString())).thenReturn("personalNumber");
        when(mockEngagemangBegaranRequestMapper.createEngagemangBegaranRequest(any(BusinessEngagementsRequestDto.class))).thenReturn(new EngagemangBegaran());
        when(mockBolagsverketIntegration.callBolagsverket(any(EngagemangBegaran.class))).thenReturn(new EngagemangSvar());
        when(mockEngagemangSvarMapper.mapBolagsverketResponse(any(EngagemangSvar.class))).thenReturn(BusinessEngagementsResponse.builder()
                        .withEngagements(List.of(BusinessEngagementsResponse.Engagement.builder()
                                        .withOrganizationNumber("5591628136")
                                        .withOrganizationName("Something IT AB")
                                .build()))
                .build());
        when(mockLegalEntityIntegration.getGuidForOrganization(eq("5591628136"))).thenReturn("uuid1");
        
        final BusinessEngagementsResponse response = service.getBusinessEngagements(TestObjectFactory.createDummyRequestDto());
    
        verify(mockCitizenMappingIntegration, times(1)).getCitizenMapping(anyString());
        verify(mockEngagemangBegaranRequestMapper, times(1)).createEngagemangBegaranRequest((any(BusinessEngagementsRequestDto.class)));
        verify(mockBolagsverketIntegration, times(1)).callBolagsverket(any(EngagemangBegaran.class));
        verify(mockEngagemangSvarMapper, times(1)).mapBolagsverketResponse(any(EngagemangSvar.class));
        
        
        assertThat(response.getEngagements().stream().anyMatch(engagement ->
                    engagement.getOrganizationNumber().equals("5591628136") &&
                    engagement.getOrganizationId().equals("uuid1") &&
                    engagement.getOrganizationName().equals("Something IT AB")
                )).isTrue();
    }
    @Test
    void testGetBusinessEngagements_getCitizenMappingThrowsException_shouldThrowException() {
        when(mockCitizenMappingIntegration.getCitizenMapping(anyString())).thenThrow(Problem.builder().withDetail("Something wrong").build());
    
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> service.getBusinessEngagements(TestObjectFactory.createDummyRequestDto()))
                .withMessage("Something wrong");
    
        verify(mockCitizenMappingIntegration, times(1)).getCitizenMapping(anyString());
        verify(mockEngagemangBegaranRequestMapper, times(0)).createEngagemangBegaranRequest((any(BusinessEngagementsRequestDto.class)));
        verify(mockBolagsverketIntegration, times(0)).callBolagsverket(any(EngagemangBegaran.class));
        verify(mockEngagemangSvarMapper, times(0)).mapBolagsverketResponse(any(EngagemangSvar.class));
    }
    
    @Test
    void testGetBusinessEngagements_bolagsverketThrowsException_shouldThrowException() {
        when(mockCitizenMappingIntegration.getCitizenMapping(anyString())).thenReturn("personalNumber");
        when(mockEngagemangBegaranRequestMapper.createEngagemangBegaranRequest(any(BusinessEngagementsRequestDto.class))).thenReturn(new EngagemangBegaran());
    
        when(mockBolagsverketIntegration.callBolagsverket(any(EngagemangBegaran.class))).thenThrow(Problem.builder().withDetail("Something wrong").build());
        
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() -> service.getBusinessEngagements(TestObjectFactory.createDummyRequestDto()))
                .withMessage("Something wrong");
        
        verify(mockCitizenMappingIntegration, times(1)).getCitizenMapping(anyString());
        verify(mockEngagemangBegaranRequestMapper, times(1)).createEngagemangBegaranRequest((any(BusinessEngagementsRequestDto.class)));
        verify(mockBolagsverketIntegration, times(1)).callBolagsverket(any(EngagemangBegaran.class));
        verify(mockEngagemangSvarMapper, times(0)).mapBolagsverketResponse(any(EngagemangSvar.class));
    }
    
    @Test
    void testGetResponseIfCached_shouldReturnOptionalEmpty_whenNotFound(final SoftAssertions softly) {
        String partyId = "abc123";
        when(mockRepository.findByPartyId(anyString())).thenReturn(Optional.empty());
        final Optional<BusinessEngagementsResponse> responseIfCached = service.getResponseIfCached(partyId);
    
        softly.assertThat(responseIfCached.isPresent()).isEqualTo(false);
    }
    
    @Test
    void testFetchAndPopulateGuid() {
        BusinessEngagementsResponse response = new BusinessEngagementsResponse();
        response.addEngagement(BusinessEngagementsResponse.Engagement.builder().withOrganizationNumber("123456").build());
        response.addEngagement(BusinessEngagementsResponse.Engagement.builder().withOrganizationNumber("654321").build());
        response.addEngagement(BusinessEngagementsResponse.Engagement.builder().withOrganizationNumber("987654").build());
        
        when(mockLegalEntityIntegration.getGuidForOrganization(eq("123456"))).thenReturn("uuid1");
        when(mockLegalEntityIntegration.getGuidForOrganization(eq("654321"))).thenReturn("uuid2");
        //Test that we got not uuid
        when(mockLegalEntityIntegration.getGuidForOrganization(eq("987654"))).thenReturn("");
        
        service.fetchAndPopulateGuidForOrganizations(response);
        
        assertThat(response.getEngagements().stream().anyMatch(engagement -> engagement.getOrganizationNumber().equalsIgnoreCase("123456") && engagement.getOrganizationId().equals("uuid1"))).isTrue();
        assertThat(response.getEngagements().stream().anyMatch(engagement -> engagement.getOrganizationNumber().equalsIgnoreCase("654321") && engagement.getOrganizationId().equals("uuid2"))).isTrue();
        assertThat(response.getEngagements().stream().anyMatch(engagement -> engagement.getOrganizationNumber().equalsIgnoreCase("987654") && engagement.getOrganizationId() == null)).isTrue();    //No uuid
        assertThat(response.getStatusDescriptions().containsValue("Couldn't fetch guid for organization number")).isTrue(); //Make sure we have a status description.
    }
    
    @Test
    void testHandleNewCacheEntity_shouldStore_whenNoFaultyContent() {
        BusinessEngagementsResponse response = BusinessEngagementsResponse.builder().build();
        
        service.handleNewCacheEntity(response, "partyId");
        
        verify(mockRepository, times(1)).deleteByPartyId(eq("partyId"));
        verify(mockRepository, times(1)).save(any(EngagementsCacheEntity.class));
        
    }
    
    @Test
    void testHandleNewCacheEntity_shouldNotStore_whenFaultyContent() {
        BusinessEngagementsResponse response = BusinessEngagementsResponse.builder()
                .withStatusDescriptions(new HashMap<>(){{put("NOK", "Something wrong");}})
                .build();
        service.handleNewCacheEntity(response, "partyId");
        
        verify(mockRepository, times(0)).deleteByPartyId(anyString());
        verify(mockRepository, times(0)).save(any(EngagementsCacheEntity.class));
    }
    
}