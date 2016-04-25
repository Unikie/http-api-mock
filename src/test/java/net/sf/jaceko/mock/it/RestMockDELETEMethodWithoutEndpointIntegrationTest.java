package net.sf.jaceko.mock.it;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Integration tests of REST mock, DELETE method
 *
 * @author Jacek Obarymski
 */
public class RestMockDELETEMethodWithoutEndpointIntegrationTest {

    //mocked endpoints configured in ws-mock.properties
    private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest";
    private static final String REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled";

    private final RestMockDELETEMethodIntegrationTestHelper testHelper = new RestMockDELETEMethodIntegrationTestHelper();

    @Before
    public void initMock() throws IOException {
        testHelper.initMock();
    }

    @Test
    public void shouldReturnDefaultRESTPostResponse()
        throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnDefaultRESTPostResponse(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode(REST_MOCK_ENDPOINT);
    }


    @Test
    public void shouldReturnCustomRESTDeleteResponseBodyAndCode() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTDeleteResponseBodyAndCode(REST_MOCK_ENDPOINT);

    }

    @Test
    public void shouldReturnConsecutiveCustomRESTDeleteResponses() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomRESTDeleteResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode_WhilePassingResourceId() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode_WhilePassingResourceId(REST_MOCK_ENDPOINT);
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
}
