package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.bolagsverket.schema.ssbt.metadata.Part;
import se.sundsvall.businessengagements.TestObjectFactory;

@ExtendWith(SoftAssertionsExtension.class)
class AnvandareMapperTest {
    
    private final AnvandareMapper mapper = new AnvandareMapper();
    
    @Test
    void createAnvandare(final SoftAssertions softly) {
        final Part anvandare = mapper.createAnvandare(TestObjectFactory.createDummyRequestDto());
    
        softly.assertThat(anvandare.getPartId().getPersonnummer()).isEqualTo("198001011234");
        softly.assertThat(anvandare.getPartNamn()).isEqualTo("John Doe");
    }
}
