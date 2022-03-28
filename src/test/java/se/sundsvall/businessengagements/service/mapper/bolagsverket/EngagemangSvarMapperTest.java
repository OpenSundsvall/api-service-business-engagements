package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangSvar;
import se.sundsvall.businessengagements.TestObjectFactory;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;

/**
 * Only tests one "happy" and one "fail" case, more cases will be tested in integration tests.
 * Mainly to get test coverage for all the methods and make sure they provide the correct data.
 */
@ExtendWith(SoftAssertionsExtension.class)
class EngagemangSvarMapperTest {
    
    private final EngagemangSvarMapper mapper = new EngagemangSvarMapper();
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    @Test
    void testMapBolagsverketResponse(final SoftAssertions softly) throws Exception {
        EngagemangSvar engagemangSvar = TestObjectFactory.getEngagemangSvar(TestObjectFactory.getHappyFileAsString());
    
        BusinessEngagementsResponse response = mapper.mapBolagsverketResponse(engagemangSvar);
    
        softly.assertThat(response.getEngagements().size()).isEqualTo(6);
        softly.assertThat(responseContainsName(response, "Jons barnvagnar")).isTrue();
        softly.assertThat(responseContainsName(response, "Jons skämtartiklar")).isTrue();
        softly.assertThat(responseContainsName(response, "Mystery Productions AB")).isTrue();
        softly.assertThat(responseContainsName(response, "Biotech AB")).isTrue();
        softly.assertThat(responseContainsName(response, "Bostadsrättsföreningen Simsalabim")).isTrue();
        softly.assertThat(responseContainsName(response, "Enskild näringsverksamhet")).isTrue();
        
        softly.assertThat(resposneContainsOrganizationNumber(response, "198001011234")).isTrue();
        softly.assertThat(resposneContainsOrganizationNumber(response, "198001011235")).isTrue();
        softly.assertThat(resposneContainsOrganizationNumber(response, "198001011236")).isTrue();
        softly.assertThat(resposneContainsOrganizationNumber(response, "5560000002")).isTrue();
        softly.assertThat(resposneContainsOrganizationNumber(response, "5560000003")).isTrue();
        softly.assertThat(resposneContainsOrganizationNumber(response, "7691234567")).isTrue();
    }
    
    @Test
    void testMapFailedResponseFromBolagsverket(final SoftAssertions softly) throws Exception {
        EngagemangSvar engagemangSvar = TestObjectFactory.getEngagemangSvar(TestObjectFactory.getFailFileAsString());
    
        BusinessEngagementsResponse response = mapper.mapBolagsverketResponse(engagemangSvar);
    
        softly.assertThat(response.getStatusDescriptions().size()).isEqualTo(2);
        softly.assertThat(response.getStatusDescriptions().get("No response from underlying system.")).contains("Kan inte upprätta anslutning med dataproducenten");
        softly.assertThat(response.getStatusDescriptions().get("Timeout.")).contains("Underliggande system Y överskred begärd svarstid");
    }
    
    private boolean responseContainsName(BusinessEngagementsResponse response, String orgName) {
        return response.getEngagements().stream()
                .anyMatch(engagement -> engagement.getOrganizationName().equalsIgnoreCase(orgName));
    }
    
    private boolean resposneContainsOrganizationNumber(BusinessEngagementsResponse response, String orgNumber) {
        return response.getEngagements().stream()
                .anyMatch(engagement -> engagement.getOrganizationNumber().equalsIgnoreCase(orgNumber));
    }

}