package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import se.sundsvall.businessengagements.BusinessEngagements;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/BusinessEngagementsIT/", classes = BusinessEngagements.class)

public class BusinessEngagementsIT extends AbstractAppTest {
    
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)   //Since data is persisted in the H2 we need to reset it in methods that use the same data
    @Test
    void test1_successful() throws Exception {
        String partyId = "e57e9dec-4132-11ec-973a-0242ac130003";   //For clarity, this is what we match the request on.
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
    
    @Test
    void test2_okResponseButTimeoutFromBolagsverket() throws Exception {
        String partyId = "e57e9ffe-4132-11ec-973a-0242ac130003";
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
    
    @Test
    void test3_noEngagementsShouldReturnNoContent() throws Exception {
        String partyId = "e57ea0ee-4132-11ec-973a-0242ac130003";
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.NO_CONTENT)
                .sendRequestAndVerifyResponse();
    }
    
    @Test
    void test4_errorResponseFromBolagsverket_shouldReturnError() throws Exception {
        String partyId = "e57ea1b6-4132-11ec-973a-0242ac130003";
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
    
    @Test
    void test5_okResponseButInternalErrorsFromBothBolagsverketAndSkatteverket() throws Exception {
        String partyId = "e57ea274-4132-11ec-973a-0242ac130003";
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
    
    @Test
    void test6_timeoutFromBolagsverket_shouldThrowException() throws Exception {
        String partyId = "522b52c1-c34d-4f80-b637-29288b08d6dc";
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
    
    /**
     * Faking a 404 from LegalEntity for "org-no" 198001011234.
     * @throws Exception
     */
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD) //Since data is persisted in the H2 we need to reset it in methods that use the same data
    @Test
    void test7_missingGuidFromLegalEntity_shouldPopulateStatusDescription() throws Exception {
        String partyId = "e57e9dec-4132-11ec-973a-0242ac130003";   //For clarity, this is what we match the request on.
        setupCall()
                .withServicePath("/engagements/" + partyId + "?personalName=Jane%20Doe&serviceName=Kommunen")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("expected.json")
                .sendRequestAndVerifyResponse();
    }
}

