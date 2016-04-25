package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.util.FileReader;
import org.apache.commons.httpclient.HttpStatus;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Created by esa on 12.1.2015.
 */
public class RestMockPUTMethodIntegrationTestHelper {
    private static final String REST_MOCK_PUT_INIT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/init";
    private static final String REST_MOCK_PUT_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/responses";
    private static final String REST_MOCK_PUT_RECORDED_REQUESTS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-requests";
    private static final String REST_MOCK_PUT_RECORDED_REQUESTS_WITH_REQUEST_ELEMENT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-requests?requestElement=req";
    private static final String REST_MOCK_PUT_RECORDED_RESOURCE_IDS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-resource-paths";
    private static final String REST_MOCK_PUT_RECORDED_REQUESTS_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-request-headers";

    private static final String REST_MOCK_PUT_INIT_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/PUT/init";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/PUT/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/PUT/recorded-resource-paths";

    private final HttpRequestSender requestSender = new HttpRequestSender();

    public void initMock() throws IOException {
        // initalizing mock, clearing history of previous requests
        requestSender.sendPostRequest(REST_MOCK_PUT_INIT, "", MediaType.TEXT_XML);
        requestSender.sendPostRequest(REST_MOCK_PUT_INIT_FOR_PATH_ENABLED_SERVICE, "", MediaType.TEXT_XML);
    }

    public void shouldReturnDefaultRESTPostResponse(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        MockResponse response = requestSender.sendPutRequest(restMockEndpoint, "", MediaType.TEXT_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//put_response_data", equalTo("default REST PUT response text")));
    }

    public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        String customResponseXML = "<custom_response>custom REST PUT response text</custom_response>";
        requestSender.sendPostRequest(REST_MOCK_PUT_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        // sending REST PUT request
        MockResponse response = requestSender.sendPutRequest(restMockEndpoint, "", MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_response", equalTo("custom REST PUT response text")));

        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
    }

    public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode_WhilePassingResourceId(String restMockEndpoint)
        throws IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        String customResponseXML = "<custom_put_response>custom REST PUT response text</custom_put_response>";
        requestSender.sendPostRequest(REST_MOCK_PUT_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        // sending REST PUT request
        MockResponse response = requestSender.sendPutRequest(restMockEndpoint + "/someResourceId", "", MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_put_response", equalTo("custom REST PUT response text")));

        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
    }

    public void shouldVerifyResourceIds(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        requestSender.sendPutRequest(restMockEndpoint + "/id1", "", MediaType.TEXT_XML);
        requestSender.sendPutRequest(restMockEndpoint + "/id2", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_PUT_RECORDED_RESOURCE_IDS);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[1]", equalTo("id1")));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[2]", equalTo("id2")));

    }

    public void shouldReturnCustomRESTPutResponseBodyAndCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        String customResponseXML = "<custom_response>conflict</custom_response>";
        requestSender.sendPostRequest(REST_MOCK_PUT_RESPONSES + "?code=409", customResponseXML, MediaType.TEXT_XML);

        // sending REST PUT request
        MockResponse response = requestSender.sendPutRequest(restMockEndpoint, "", MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc, hasXPath("//custom_response", equalTo("conflict")));

        assertThat("custom response code", response.getCode(), is(HttpStatus.SC_CONFLICT));

    }

    public void shouldReturnConsecutiveCustomRESTPostResponses(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up consecutive responses on mock
        String customResponseXML1 = "<custom_put_response>custom REST PUT response text 1</custom_put_response>";
        requestSender.sendPutRequest(REST_MOCK_PUT_RESPONSES + "/1", customResponseXML1, MediaType.TEXT_XML);

        String customResponseXML2 = "<custom_put_response>custom REST PUT response text 2</custom_put_response>";
        requestSender.sendPutRequest(REST_MOCK_PUT_RESPONSES + "/2", customResponseXML2, MediaType.TEXT_XML);

        MockResponse response = requestSender.sendPutRequest(restMockEndpoint, "", MediaType.TEXT_XML);
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//custom_put_response", equalTo("custom REST PUT response text 1")));

        response = requestSender.sendPutRequest(restMockEndpoint, "", MediaType.TEXT_XML);
        serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//custom_put_response", equalTo("custom REST PUT response text 2")));
    }

    public void shouldVerifyRecordedRequests(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        requestSender.sendPutRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.TEXT_XML);
        requestSender.sendPutRequest(restMockEndpoint, "<dummyReq>dummyReqText2</dummyReq>", MediaType.TEXT_XML);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_PUT_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[1]", equalTo("dummyReqText1")));
        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[2]", equalTo("dummyReqText2")));

    }

    public void shouldVerifyRecordedRequestsWithCustomRequestElement(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        requestSender.sendPutRequest(restMockEndpoint, "dummyReqText1", MediaType.TEXT_XML);
        requestSender.sendPutRequest(restMockEndpoint, "dummyReqText2", MediaType.TEXT_XML);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_PUT_RECORDED_REQUESTS_WITH_REQUEST_ELEMENT);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/req[1]", equalTo("dummyReqText1")));
        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/req[2]", equalTo("dummyReqText2")));

    }

    public void shouldVerifyRecordedRequestsWithHeaders(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader", "aValue");

        // Given - We send a post request to the end point with headers
        requestSender.sendPutRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.APPLICATION_XML, headers);

        //When we get the recorded request headers
        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_PUT_RECORDED_REQUESTS_HEADERS);

        // Then the header we sent is returned
        assertThat("Expected a response body", recordedRequests.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(recordedRequests.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

    public void shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendPutRequest(restMockEndpoint + "/id123/id234", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE);
        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendPutRequest(restMockEndpoint + "/id123", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS);
        assertThat("Expected HTTP 404 status code as resource paths are not enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldVerifyResourcePaths(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        String path1 = "id123/id234/id345";
        String path2 = "id567/id678/id789";
        requestSender.sendPutRequest(restMockEndpoint + "/" + path1, "", MediaType.TEXT_XML);
        requestSender.sendPutRequest(restMockEndpoint + "/" + path2, "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[1]", equalTo(path1)));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[2]", equalTo(path2)));
    }

    public void shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        String path = "id123/id234/id345";
        MockResponse mockResponse = requestSender.sendPutRequest(restMockEndpoint + "/" + path, "", MediaType.TEXT_XML);

        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", mockResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldHandleRequestWithAttachment(String restMockEndpoint) throws IOException, SAXException, ParserConfigurationException {
        List<HttpRequestSender.RestAttachment> attachments = new ArrayList<HttpRequestSender.RestAttachment>();
        attachments.add(new HttpRequestSender.RestAttachment(MediaType.APPLICATION_OCTET_STREAM, new FileReader().readBinaryFileContents(RestMockPOSTMethodIntegrationTestHelper.BINARY_ATTACHMENT_FILE_PATH)));

        requestSender.sendPutRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.APPLICATION_XML, Collections.EMPTY_MAP, attachments);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_PUT_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[1]", equalTo("dummyReqText1")));
    }
}
