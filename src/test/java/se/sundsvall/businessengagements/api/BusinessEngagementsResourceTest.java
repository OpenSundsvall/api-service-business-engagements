package se.sundsvall.businessengagements.api;

import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;
import se.sundsvall.businessengagements.service.BusinessEngagementsService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class })
class BusinessEngagementsResourceTest {
    
    @Mock
    private BusinessEngagementsService service;
    
    private BusinessEngagementsResource resource;
    
    @BeforeEach
    public void setup() {
        resource = new BusinessEngagementsResource(service);
    }
    
    @Test
    void testGetEngagements_invoicesReturned_shouldReturnOkResponse(final SoftAssertions softly) {
        BusinessEngagementsResponse response = new BusinessEngagementsResponse();
        response.addEngagement("orgname", "orgnumber");
        
        when(service.getBusinessEngagements(any(BusinessEngagementsRequestDto.class))).thenReturn(response);
    
        final ResponseEntity<BusinessEngagementsResponse> engagements = resource.getEngagements("abc123", "John Doe", "serviceName");
        final BusinessEngagementsResponse businessEngagementsResponse = engagements.getBody();
    
        softly.assertThat(engagements.getStatusCodeValue()).isEqualTo(HttpStatus.SC_OK);
        softly.assertThat(businessEngagementsResponse.getStatus()).isEqualTo(BusinessEngagementsResponse.Status.OK);
    }
    
    @Test
    void testGetEngagements_noInvoices_shouldReturnNoContent(final SoftAssertions softly) {
        when(service.getBusinessEngagements(any(BusinessEngagementsRequestDto.class))).thenReturn(new BusinessEngagementsResponse());
    
        final ResponseEntity<BusinessEngagementsResponse> engagements = resource.getEngagements("abc123", "John Doe", "serviceName");
    
        softly.assertThat(engagements.getStatusCodeValue()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }
    
    @Test
    void testGetEngagements_populatedStatusDescription_shouldReturnOkResponse(final SoftAssertions softly) {
        BusinessEngagementsResponse response = new BusinessEngagementsResponse();
        response.addEngagement("orgname", "orgnumber");
        response.addStatusDescription("type", "description");
        
        when(service.getBusinessEngagements(any(BusinessEngagementsRequestDto.class))).thenReturn(response);
    
        final ResponseEntity<BusinessEngagementsResponse> engagements = resource.getEngagements("abc123", "John Doe", "serviceName");
    
        softly.assertThat(engagements.getStatusCodeValue()).isEqualTo(HttpStatus.SC_OK);
    }
    
    /**
     * Not really applicable during runtime but what the heck.
     */
    /*@Test
    void testGetEngagements_exception() {
        when(service.getBusinessEngagements(any(BusinessEngagementsRequestDto.class))).thenThrow(Problem.builder().withDetail("Something wrong").build());
    
        assertThatExceptionOfType(ThrowableProblem.class).isThrownBy(() ->
                resource.getEngagements("test", "test", "test", "direct"))
                .withMessage("Something wrong");
    }*/
}