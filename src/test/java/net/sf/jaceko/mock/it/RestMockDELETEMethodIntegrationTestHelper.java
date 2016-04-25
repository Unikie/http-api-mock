package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Created by esa on 2.1.2015.
 */
public class RestMockDELETEMethodIntegrationTestHelper {
    private static final String REST_MOCK_DELETE_INIT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/init";
    private static final String REST_MOCK_DELETE_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/responses";

    private static final String REST_MOCK_DELETE_RECORDED_REQUESTS_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/recorded-request-headers";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/recorded-resource-paths";

    private static final String REST_MOCK_DELETE_INIT_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/DELETE/init";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/DELETE/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/DELETE/recorded-resource-paths";


    private final HttpRequestSender requestSender = new HttpRequestSender();

    public void initMock() throws IOException {
        requestSender.sendPostRequest(REST_MOCK_DELETE_INIT, "", MediaType.TEXT_XML);
        requestSender.sendPostRequest(REST_MOCK_DELETE_INIT_FOR_PATH_ENABLED_SERVICE, "", MediaType.TEXT_XML);
    }

    public void shouldReturnDefaultRESTPostResponse(String restMockEndpoint)
        throws IOException, ParserConfigurationException, SAXException {

        MockResponse response = requestSender.sendDeleteRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(
            serviceResponseDoc,
            hasXPath("//delete_response_data",
                equalTo("default REST DELETE response text")));
    }

    public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        //setting up response body on mock
        //not setting custom response code
        String customResponseXML = "<custom_response>custom REST DELETE response text</custom_response>";
        requestSender.sendPostRequest(REST_MOCK_DELETE_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        //sending REST DELETE request
        MockResponse response = requestSender.sendDeleteRequest(restMockEndpoint);


        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_response",
                equalTo("custom REST DELETE response text")));
    }

    public void shouldReturnCustomRESTDeleteResponseBodyAndCode(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        String customResponseXML = "<custom_response>conflict</custom_response>";
        requestSender.sendPostRequest(REST_MOCK_DELETE_RESPONSES + "?code=409", customResponseXML, MediaType.TEXT_XML);

        //sending REST DELETE request
        MockResponse response = requestSender.sendDeleteRequest(restMockEndpoint);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_response",
                equalTo("conflict")));


        assertThat("custom response code", response.getCode(), is(HttpStatus.SC_CONFLICT));

    }

    public void shouldReturnConsecutiveCustomRESTDeleteResponses(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        //setting up consecutive responses on mock
        String customResponseXML1 = "<custom_delete_response>custom REST DELETE response text 1</custom_delete_response>";
        requestSender.sendPutRequest(REST_MOCK_DELETE_RESPONSES + "/1", customResponseXML1, MediaType.TEXT_XML);

        String customResponseXML2 = "<custom_delete_response>custom REST DELETE response text 2</custom_delete_response>";
        requestSender.sendPutRequest(REST_MOCK_DELETE_RESPONSES + "/2", customResponseXML2, MediaType.TEXT_XML);

        MockResponse response = requestSender.sendDeleteRequest(restMockEndpoint);
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(
            serviceResponseDoc,
            hasXPath("//custom_delete_response",
                equalTo("custom REST DELETE response text 1")));

        response = requestSender.sendDeleteRequest(restMockEndpoint);
        serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(
            serviceResponseDoc,
            hasXPath("//custom_delete_response",
                equalTo("custom REST DELETE response text 2")));
    }

    public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode_WhilePassingResourceId(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        //setting up response body on mock
        //not setting custom response code
        String customResponseXML = "<custom_delete_response>custom REST DELETE response text</custom_delete_response>";
        requestSender.sendPostRequest(REST_MOCK_DELETE_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        //sending REST DELETE request
        MockResponse response = requestSender.sendDeleteRequest(restMockEndpoint + "/someResourceId");

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_delete_response",
                equalTo("custom REST DELETE response text")));


        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
    }

    public void shouldVerifyRecordedRequestsWithHeaders(String restMockEndpoint) throws Exception {
        // Given we've sent a get request with headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader", "aValue");
        requestSender.sendDeleteRequest(restMockEndpoint, headers);

        //When we get the recorded headers
        MockResponse recordedRequestsHeaders = requestSender.sendGetRequest(REST_MOCK_DELETE_RECORDED_REQUESTS_HEADERS);

        //Then the header sent in the Get request is returned
        assertThat("Expected a response body", recordedRequestsHeaders.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequestsHeaders.getBody());

        assertThat(recordedRequestsHeaders.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

    public void shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendDeleteRequest(restMockEndpoint + "/id123/id234");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE);
        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendDeleteRequest(restMockEndpoint + "/id123");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS);
        assertThat("Expected HTTP 404 status code as resource paths are not enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldVerifyResourcePaths(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        String path1 = "id123/id234/id345";
        String path2 = "id567/id678/id789";
        requestSender.sendDeleteRequest(restMockEndpoint + "/" + path1);
        requestSender.sendDeleteRequest(restMockEndpoint + "/" + path2);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[1]", equalTo(path1)));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[2]", equalTo(path2)));
    }

    public void shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        String path = "id123/id234/id345";
        MockResponse mockResponse = requestSender.sendDeleteRequest(restMockEndpoint + "/" + path);

        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", mockResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }
}
