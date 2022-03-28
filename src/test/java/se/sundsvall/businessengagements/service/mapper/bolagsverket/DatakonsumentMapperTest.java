package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.bolagsverket.schema.ssbt.metadata.Datakonsument;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;

@ExtendWith(SoftAssertionsExtension.class)
class DatakonsumentMapperTest {
    
    DatakonsumentMapper mapper = new DatakonsumentMapper();
    
    @Test
    void createDatakonsument(final SoftAssertions softly) {
        final Datakonsument datakonsument = mapper.createDatakonsument(BusinessEngagementsRequestDto.builder()
                .withPersonalNumber("190101011234")
                .build());
    
        softly.assertThat(datakonsument.getService().getServiceNamn()).isEqualTo("BUSENGBV");
        softly.assertThat(datakonsument.getPartId().getOrganisationsnummer()).isEqualTo("2120002411");
        softly.assertThat(datakonsument.getPartNamn()).isEqualTo("SundsvallsKommun");
    }
}