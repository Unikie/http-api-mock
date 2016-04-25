package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

/**
 * Created by esa on 7.1.2015.
 */
public class RestMockGETJsonIntegrationTestHelper {

    private static final String REST_MOCK_GET_INIT = "http://localhost:8080/mock/services/REST/dummy-rest-json/operations/GET/init";
    private static final String REST_MOCK_GET_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest-json/operations/GET/responses";

    private final HttpRequestSender requestSender = new HttpRequestSender();


    public void initMock() throws IOException {
        // initalizing mock, clearing history of previous requests
        requestSender.sendPostRequest(REST_MOCK_GET_INIT, "", MediaType.TEXT_XML);
    }

    // default json response defined in ws-mock.properties
    public void shouldReturnDefaultJsonResponse(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        assertThat(response.getContentType(), is(APPLICATION_JSON_TYPE.toString()));
        assertThat(response.getBody(),
            sameJSONAs("{'myArray': [{ 'name': 'John Doe', 'age': 29 },{ 'name': 'Anna Smith', 'age': 24 }]}"));
    }

    public void shouldReturnCustomRESTGetResponseBody(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        String customResponseJson = "{'myArray': [{ 'name': 'Jan Kowalski', 'age': 33 },{ 'name': 'John Smith', 'age': 34 }]}";
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES, customResponseJson, MediaType.APPLICATION_JSON);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_TYPE.toString()));
        assertThat(response.getBody(), sameJSONAs(customResponseJson));
    }
}
