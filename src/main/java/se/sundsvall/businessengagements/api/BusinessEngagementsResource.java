package se.sundsvall.businessengagements.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse;
import se.sundsvall.businessengagements.api.model.BusinessEngagementsResponse.Status;
import se.sundsvall.businessengagements.api.model.validation.ValidPartyId;
import se.sundsvall.businessengagements.domain.dto.BusinessEngagementsRequestDto;
import se.sundsvall.businessengagements.service.BusinessEngagementsService;

@RestController
@RequestMapping(value = "/engagements")
@Tag(name = "Business Engagements", description = "En tjänst som hämtar en persons företagsengagemang")
public class BusinessEngagementsResource {
    
    private final BusinessEngagementsService businessEngagementsService;
    
    public BusinessEngagementsResource(final BusinessEngagementsService businessEngagementsService) {
        this.businessEngagementsService = businessEngagementsService;
    }
    
    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = BusinessEngagementsResponse.class)))
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = Problem.class)))
    @GetMapping("/{partyId}")
    public ResponseEntity<BusinessEngagementsResponse> getEngagements(
            @ValidPartyId
            @PathVariable("partyId") @Schema(description = "Unique identifier for a person", example = "6a5c3d04-412d-11ec-973a-0242ac130003") final String partyId,
            @RequestParam("personalName") @Schema(description = "The first and surname of the person") final String personalName,
            @RequestParam("serviceName") @Schema(description = "Name of the system initiating the request", example = "Mina Sidor") final String serviceName
            ) {
    
        BusinessEngagementsRequestDto requestDto = new BusinessEngagementsRequestDto(personalName, null, partyId, serviceName);   //We don't know personalNumber yet
        final BusinessEngagementsResponse response = businessEngagementsService.getBusinessEngagements(requestDto);
        
        // A little janky but if there's a statusDescription it means something went wrong and we should indicate this with a NOK as status.
        if(response.getStatusDescriptions() != null) {
            response.setStatus(Status.NOK);
        } else {
            response.setStatus(Status.OK);
        }
        
        if(response.getEngagements() == null && response.getStatusDescriptions() == null) {
            //In this case we have an empty answer and nothing has gone wrong.
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(response);
    }
}