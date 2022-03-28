package se.sundsvall.businessengagements.api.model;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class BusinessEngagementsResponseTest {
    
    private final BusinessEngagementsResponse response = new BusinessEngagementsResponse();
    
    @Test
    void testAddEngagementWithValues(final SoftAssertions softly) {
        response.addEngagement("A Company", "16321654987");
        
        softly.assertThat(response.getEngagements().get(0).getOrganizationName()).isEqualTo("A Company");
        softly.assertThat(response.getEngagements().get(0).getOrganizationNumber()).isEqualTo("16321654987");
    }
}