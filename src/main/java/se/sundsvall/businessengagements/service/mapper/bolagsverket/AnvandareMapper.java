package se.sundsvall.businessengagements.service.mapper.bolagsverket;

import se.bolagsverket.schema.ssbt.metadata.ObjectFactory;
import se.bolagsverket.schema.ssbt.metadata.Part;
import se.bolagsverket.schema.ssbt.metadata.PartId;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;

import org.springframework.stereotype.Component;

/**
 * <pre>
 * Creates the following xml:
 * {@code
 *   <md:Anvandare>
 *       <md:PartId>
 *           <md:Personnummer>198001011234</md:Personnummer>
 *       </md:PartId>
 *       <md:PartNamn>Jon Doe</md:PartNamn>
 *   </md:Anvandare>
 * }
 * </pre>
 */
@Component
public class AnvandareMapper {

    public Part createAnvandare(final BusinessEngagementsRequestDto requestDto) {
        ObjectFactory objectFactory = new ObjectFactory();
    
        PartId partId = objectFactory.createPartId()
                .withPersonnummer(requestDto.getPersonalNumber());
    
        return objectFactory.createPart()
                .withPartId(partId)
                .withPartNamn(requestDto.getPersonalName());
    }
}
