package net.sf.jaceko.mock.it;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Integration tests of REST mock returning json default response
 *
 * @author Jacek Obarymski
 */
public class RestMockGETJsonWithoutEndpointIntegrationTest {

    // mocked endpoints configured in ws-mock.properties
    private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest-json";

    private final RestMockGETJsonIntegrationTestHelper testHelper = new RestMockGETJsonIntegrationTestHelper();

    @Before
    public void initMock() throws IOException {
        testHelper.initMock();
    }

    @Test
    public void shouldReturnDefaultJsonResponse() throws IOException, ParserConfigurationException,
        SAXException {
        testHelper.shouldReturnDefaultJsonResponse(REST_MOCK_ENDPOINT);
    }

    @Test
    public void shouldReturnCustomRESTGetResponseBody() throws
        IOException, ParserConfigurationException, SAXException {
        testHelper.shouldReturnCustomRESTGetResponseBody(REST_MOCK_ENDPOINT);
    }

}
