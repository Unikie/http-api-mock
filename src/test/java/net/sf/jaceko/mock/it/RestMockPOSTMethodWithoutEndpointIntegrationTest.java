package net.sf.jaceko.mock.it;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Integration tests of REST mock, POST method
 *
 * @author Jacek Obarymski
 */
public class RestMockPOSTMethodWithoutEndpointIntegrationTest {

    // mocked endpoints configured in ws-mock.properties
    private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest";
    private static final String REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled";

    private final RestMockPOSTMethodIntegrationTestHelper testHelper = new RestMockPOSTMethodIntegrationTestHelper();

    @Before
    public void initMock() throws IOException {
        testHelper.initMock();
    }

    @Test
    public void shouldReturnDefaultResponse() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldReturnDefaultResponse(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldAcceptVendorSpecificMimeType() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldAcceptVendorSpecificMimeType(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomXmlResponseBodyAndDefaultResponseCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomXmlResponseBodyAndDefaultResponseCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomJsonResponseBody() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomJsonResponseBody(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomXmlResponseBodyAndCode() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomXmlResponseBodyAndCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnConsecutiveCustomXmlResponses() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomXmlResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnConsecutiveCustomXmlResponses2() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomXmlResponses2(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnConsecutiveCustomJsonResponses() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomJsonResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldDelayResponseFor1sec() throws IOException {
        testHelper.shouldDelayResponseFor1sec(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldDelaySecondResponseFor1Sec() throws IOException {
        testHelper.shouldDelaySecondResponseFor1Sec(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequests() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldVerifyRecordedRequests(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequestsUsingRequestElement() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldVerifyRecordedRequestsUsingRequestElement(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedJsonRequest() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldVerifyRecordedJsonRequest(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRequestParameters() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldVerifyRequestParameters(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequestsWithHeaders() throws Exception {
        testHelper.shouldVerifyRecordedRequestsWithHeaders(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyResourceIds() throws ParserConfigurationException, SAXException, IOException {
        testHelper.shouldVerifyResourceIds(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTPostResponseBodyAndDefaultResponseCode_WhilePassingResourceId() throws ParserConfigurationException, SAXException, IOException {
        testHelper.shouldReturnCustomRESTPostResponseBodyAndDefaultResponseCode_WhilePassingResourceId(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTPostResponseBodyAndSpecifiedResponseCode_WhilePassingResourceId() throws ParserConfigurationException, SAXException, IOException {
        testHelper.shouldReturnCustomRESTPostResponseBodyAndSpecifiedResponseCode_WhilePassingResourceId(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled() throws IOException {
        testHelper.shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled(REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED);
    }

    @Test
    public void shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled() throws IOException {
        testHelper.shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyResourcePaths() throws ParserConfigurationException, SAXException, IOException {
        testHelper.shouldVerifyResourcePaths(REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED);
    }

    @Test
    public void shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled() throws IOException {
        testHelper.shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled(REST_MOCK_ENDPOINT);
    }
}
