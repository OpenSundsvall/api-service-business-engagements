package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.bolagsverket.schema.ssbt.metadata.Anvandningsomrade;
import se.bolagsverket.schema.ssbt.metadata.Datakonsument;
import se.bolagsverket.schema.ssbt.metadata.Part;
import se.bolagsverket.schema.ssbt.metadata.Tidsenhet;
import se.bolagsverket.schema.ssbten.engagemang.EngagemangBegaran;
import se.sundsvall.businessengagements.TestObjectFactory;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( { MockitoExtension.class, SoftAssertionsExtension.class})
class EngagemangBegaranRequestMapperTest {
    
    @Mock
    private DatakonsumentMapper datakonsumentMapperMock;
    
    @Mock
    private EngagemangBegaranDetaljerMapper engagemangBegaranDetaljerMapperMock;
    
    @Mock
    private AnvandareMapper anvandareMapperMock;
    
    private EngagemangBegaranRequestMapper requestMapper;
    
    @BeforeEach
    public void setup() {
        requestMapper = new EngagemangBegaranRequestMapper(datakonsumentMapperMock, engagemangBegaranDetaljerMapperMock, anvandareMapperMock);
    }
    
    /**
     * Only tests the parts that are not mocked away, those will be tested separately.
     * @param softly
     */
    @Test
    void testCreateEngagemangBegaranRequest(final SoftAssertions softly) {
        BusinessEngagementsRequestDto requestDto = TestObjectFactory.createDummyRequestDto();
    
        when(datakonsumentMapperMock.createDatakonsument(requestDto)).thenReturn(new Datakonsument());
        when(anvandareMapperMock.createAnvandare(any(BusinessEngagementsRequestDto.class))).thenReturn(new Part());
    
        final EngagemangBegaran engagemangBegaranRequest = requestMapper.createEngagemangBegaranRequest(requestDto);
        
        verify(datakonsumentMapperMock, times(1)).createDatakonsument(requestDto);
        verify(anvandareMapperMock, times(1)).createAnvandare(any(BusinessEngagementsRequestDto.class));
    
        softly.assertThat(engagemangBegaranRequest.getEngagemangBegaranMetadata().getTidstampel()).isNotNull();
        softly.assertThat(engagemangBegaranRequest.getEngagemangBegaranMetadata().getTTL().getTidsenhet()).isEqualTo(Tidsenhet.MILLISEKUNDER);
        softly.assertThat(engagemangBegaranRequest.getEngagemangBegaranMetadata().getAnvandningsomrade()).isEqualTo(Anvandningsomrade.INDIREKT_ATERANVANDNING);
    }
}