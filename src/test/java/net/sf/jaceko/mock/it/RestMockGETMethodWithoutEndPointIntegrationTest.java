package net.sf.jaceko.mock.it;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Integration tests of REST mock, GET method
 *
 * @author Jacek Obarymski
 */
public class RestMockGETMethodWithoutEndPointIntegrationTest {

    // mocked endpoints configured in ws-mock.properties
    private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest";
    private static final String REST_MOCK_ENDPOINT_FORBIDDEN_RESPONSE_CODE = "http://localhost:8080/mock/services/REST/dummy-rest-notauthorized";
    private static final String REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled";
    private static final String REST_MOCK_ENDPOINT_BINARY = "http://localhost:8080/mock/services/REST/dummy-rest-binary";
    private static final String REST_MOCK_ENDPOINT_WITH_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest-for-headers";

    private final RestMockGETMethodIntegrationTestHelper testHelper = new RestMockGETMethodIntegrationTestHelper();

    @Before
    public void initMock() throws IOException {
        testHelper.initMock();
    }

    @Test
    public void shouldReturnDefaultRESTGetResponse() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldReturnDefaultRESTGetResponse(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnDefaultRESTGetResponse2() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldReturnDefaultRESTGetResponse2(REST_MOCK_ENDPOINT_FORBIDDEN_RESPONSE_CODE);
    }

    @Test
    public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode_WhilePassingResourceId()
        throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode_WhilePassingResourceId(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTGetResponseBodyAndCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTGetResponseBodyAndCode(REST_MOCK_ENDPOINT);

    }

    @Test
    public void shouldReturnCustomRESTGetResponseCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTGetResponseCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnConsecutiveCustomRESTGetResponses() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomRESTGetResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnDefaultResponseCode() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldReturnDefaultResponseCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRequestParameters() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldVerifyRequestParameters(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyResourceIds() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldVerifyResourceIds(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomResponseWithHeader() throws Exception {
        testHelper.shouldReturnCustomResponseWithHeader(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomResponseWithMultipleHeaders() throws Exception {
        testHelper.shouldReturnCustomResponseWithMultipleHeaders(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomResponseHeadersSetToSpecificResponses() throws Exception {
        testHelper.shouldReturnCustomResponseHeadersSetToSpecificResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequestsWithHeaders() throws Exception {
        testHelper.shouldVerifyRecordedRequestsWithHeaders(REST_MOCK_ENDPOINT);
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

    @Test
    public void shouldReturnBinaryResponseForBinaryEnabledOperation() throws IOException {
        testHelper.shouldReturnBinaryResponseForBinaryEnabledOperation(REST_MOCK_ENDPOINT_BINARY);
    }

    @Test
    public void shouldReturnCorrectHeadersForOperationWithHeadersSet() throws IOException {
        testHelper.shouldReturnCorrectHeadersForOperationWithHeadersSet(REST_MOCK_ENDPOINT_WITH_HEADERS);
    }
}
