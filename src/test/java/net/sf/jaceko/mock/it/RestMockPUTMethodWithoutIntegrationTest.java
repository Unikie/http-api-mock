package net.sf.jaceko.mock.it;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Integration tests of REST mock, PUT method
 *
 * @author Jacek Obarymski
 */
public class RestMockPUTMethodWithoutIntegrationTest {

    // mocked endpoints configured in ws-mock.properties
    private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest";
    private static final String REST_MOCK_ENDPOINT_RESOURCE_PATHS_ENABLED = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled";

    private final RestMockPUTMethodIntegrationTestHelper testHelper = new RestMockPUTMethodIntegrationTestHelper();

    @Before
    public void initMock() throws IOException {
        testHelper.initMock();
    }

    @Test
    public void shouldReturnDefaultRESTPostResponse() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldReturnDefaultRESTPostResponse(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode_WhilePassingResourceId()
        throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode_WhilePassingResourceId(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyResourceIds() throws IOException, ParserConfigurationException, SAXException {
        testHelper.shouldVerifyResourceIds(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTPutResponseBodyAndCode() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTPutResponseBodyAndCode(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnConsecutiveCustomRESTPostResponses() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnConsecutiveCustomRESTPostResponses(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequests() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldVerifyRecordedRequests(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequestsWithCustomRequestElement() throws IOException,
        ParserConfigurationException, SAXException {
        testHelper.shouldVerifyRecordedRequestsWithCustomRequestElement(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldVerifyRecordedRequestsWithHeaders() throws ParserConfigurationException, SAXException, IOException {
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
