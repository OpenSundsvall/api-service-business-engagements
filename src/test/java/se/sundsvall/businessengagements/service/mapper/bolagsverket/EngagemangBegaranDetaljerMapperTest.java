package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaranDetaljer;
import se.sundsvall.businessengagements.TestObjectFactory;

import java.util.List;

@ExtendWith(SoftAssertionsExtension.class)
class EngagemangBegaranDetaljerMapperTest {
    
    private final EngagemangBegaranDetaljerMapper mapper = new EngagemangBegaranDetaljerMapper();
    private static final List<String> FORETAG_KODER = List.of("AB", "BRF", "E", "EK", "HB", "KB");
    
    @Test
    void testCreateEngagemangBegaranDetaljerWithIndirectUseCaseArea(final SoftAssertions softly) {
        final EngagemangBegaranDetaljer engagemang = mapper.createEngagemangBegaranDetaljer(TestObjectFactory.createDummyRequestDto());
        
        softly.assertThat(engagemang.getPersonId().getPersonIdentitetsbeteckning().getPersonnummer()).isEqualTo("198001011234");
        softly.assertThat(engagemang.getForetagsformer().getForetagsformKods()).isEqualTo(FORETAG_KODER);
        softly.assertThat(engagemang.getForetagsformer().isAllaEnskilda()).isFalse();
    }
}