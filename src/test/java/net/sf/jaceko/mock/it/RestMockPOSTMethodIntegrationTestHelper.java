package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender.RestAttachment;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.util.FileReader;
import org.apache.commons.httpclient.HttpStatus;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

/**
 * Created by esa on 2.1.2015.
 */
public class RestMockPOSTMethodIntegrationTestHelper {

    private static final String REST_MOCK_POST_INIT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/init";
    private static final String REST_MOCK_POST_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/responses";
    private static final String REST_MOCK_POST_RECORDED_REQUESTS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-requests";
    private static final String REST_MOCK_POST_RECORDED_REQUESTS_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-request-headers";
    private static final String REST_MOCK_POST_RECORDED_REQUESTS_WITH_REQUEST_ELEMENT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-requests?requestElement=request";
    private static final String REST_MOCK_POST_RECORDED_REQUEST_PARAMS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-request-params";
    private static final String REST_MOCK_POST_RECORDED_RESOURCE_IDS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-resource-paths";

    private static final String REST_MOCK_POST_INIT_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/POST/init";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/POST/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/POST/recorded-resource-paths";
    private static final String BINARY_ATTACHMENT_FILE_NAME = "onepixel.gif";
    static final String BINARY_ATTACHMENT_FILE_PATH = "it/" + BINARY_ATTACHMENT_FILE_NAME;

    private final HttpRequestSender requestSender = new HttpRequestSender();

    public void initMock() throws IOException {
        // initalizing mock, clearing history of previous requests
        requestSender.sendPostRequest(REST_MOCK_POST_INIT, "", MediaType.APPLICATION_XML);
        requestSender.sendPostRequest(REST_MOCK_POST_INIT_FOR_PATH_ENABLED_SERVICE, "", MediaType.TEXT_XML);
    }

    public void shouldReturnDefaultResponse(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {

        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_CREATED));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//post_response_data", equalTo("default REST POST response text")));
    }

    public void shouldAcceptVendorSpecificMimeType(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {

        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", "application/vnd.restbucks+xml");
        assertThat(response.getCode(), is(HttpStatus.SC_CREATED));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//post_response_data", equalTo("default REST POST response text")));
    }

    public void shouldReturnCustomXmlResponseBodyAndDefaultResponseCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up xml response body on mock
        // not setting custom response code
        String customResponseXML = "<custom_post_response>custom REST POST response text</custom_post_response>";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES, customResponseXML, MediaType.APPLICATION_XML);

        // sending REST POST request
        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body xml", serviceResponseDoc,
            hasXPath("//custom_post_response", equalTo("custom REST POST response text")));

        assertThat("default response code", response.getCode(), is(HttpStatus.SC_CREATED));
    }

    public void shouldReturnCustomJsonResponseBody(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        // setting up json response body on mock
        // not setting custom response code
        String customResponseJson = "{\"json\": \"obj\"}";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES, customResponseJson, MediaType.APPLICATION_JSON);

        // sending REST POST request
        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_JSON);
        assertThat(response.getContentType(), is(APPLICATION_JSON_TYPE.toString()));
        assertThat("custom response body xml", response.getBody(), sameJSONAs(customResponseJson));

    }

    public void shouldReturnCustomXmlResponseBodyAndCode(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        String customResponseXML = "<custom_post_response>not authorized</custom_post_response>";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=403", customResponseXML, MediaType.APPLICATION_XML);

        // sending REST POST request
        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc, hasXPath("//custom_post_response", equalTo("not authorized")));

        assertThat("custom response code", response.getCode(), is(HttpStatus.SC_FORBIDDEN));

    }

    public void shouldReturnConsecutiveCustomXmlResponses(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up consecutive responses on mock
        String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=403", customResponseXML1, MediaType.APPLICATION_XML);

        String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=200", customResponseXML2, MediaType.APPLICATION_XML);

        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//custom_post_response", equalTo("custom REST POST response text 1")));

        response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));

        serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//custom_post_response", equalTo("custom REST POST response text 2")));
    }

    public void shouldReturnConsecutiveCustomXmlResponses2(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up consecutive responses on mock
        String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
        requestSender
            .sendPutRequest(REST_MOCK_POST_RESPONSES + "/1" + "?code=403", customResponseXML1, MediaType.APPLICATION_XML);

        String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
        requestSender
            .sendPutRequest(REST_MOCK_POST_RESPONSES + "/2" + "?code=200", customResponseXML2, MediaType.APPLICATION_XML);

        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//custom_post_response", equalTo("custom REST POST response text 1")));

        response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));

        serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//custom_post_response", equalTo("custom REST POST response text 2")));
    }

    public void shouldReturnConsecutiveCustomJsonResponses(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up consecutive responses on mock
        String customResponseJson1 = "{\"custom_post_response\": \"custom REST POST response text 1\"}";
        requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/1" + "?code=403", customResponseJson1,
            MediaType.APPLICATION_JSON);

        String customResponseJson2 = "{\"custom_post_response\": \"custom REST POST response text 2\"}";
        requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/2" + "?code=200", customResponseJson2,
            MediaType.APPLICATION_JSON);

        MockResponse response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getBody(), sameJSONAs(customResponseJson1));
        assertThat(response.getContentType(), is(APPLICATION_JSON_TYPE.toString()));

        response = requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        assertThat(response.getBody(), sameJSONAs(customResponseJson2));
        assertThat(response.getContentType(), is(APPLICATION_JSON_TYPE.toString()));
    }

    public void shouldDelayResponseFor1sec(String restMockEndpoint) throws IOException {
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?delay=1", "", MediaType.APPLICATION_XML);

        Calendar before = Calendar.getInstance();
        requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        Calendar after = Calendar.getInstance();
        long oneSecInMilis = 1000l;
        assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(greaterThanOrEqualTo(oneSecInMilis)));
    }

    public void shouldDelaySecondResponseFor1Sec(String restMockEndpoint) throws IOException {
        requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/2/?delay=1", "", MediaType.APPLICATION_XML);
        long oneSecInMilis = 1000l;

        // first request is not delayed
        Calendar before = Calendar.getInstance();
        requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        Calendar after = Calendar.getInstance();
        assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(not(greaterThanOrEqualTo(oneSecInMilis))));

        // second request
        before = Calendar.getInstance();
        requestSender.sendPostRequest(restMockEndpoint, "", MediaType.APPLICATION_XML);
        after = Calendar.getInstance();
        assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(greaterThanOrEqualTo(oneSecInMilis)));

    }

    public void shouldVerifyRecordedRequests(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        requestSender.sendPostRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.APPLICATION_XML);
        requestSender.sendPostRequest(restMockEndpoint, "<dummyReq>dummyReqText2</dummyReq>", MediaType.APPLICATION_XML);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[1]", equalTo("dummyReqText1")));
        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[2]", equalTo("dummyReqText2")));
    }

    public void shouldVerifyRecordedRequestsUsingRequestElement(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        requestSender.sendPostRequest(restMockEndpoint, "dummyReqText1", MediaType.APPLICATION_XML);
        requestSender.sendPostRequest(restMockEndpoint, "dummyReqText2", MediaType.APPLICATION_XML);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS_WITH_REQUEST_ELEMENT);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/request[1]", equalTo("dummyReqText1")));
        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/request[2]", equalTo("dummyReqText2")));
    }

    public void shouldVerifyRecordedJsonRequest(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        String requestBody = "{\"dummyReq\": \"dummyReqText1\"}";
        requestSender.sendPostRequest(restMockEndpoint, requestBody, MediaType.APPLICATION_JSON);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests", containsString(requestBody)));
    }

    public void shouldVerifyRequestParameters(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        requestSender.sendPostRequest(restMockEndpoint + "?param=paramValue1", "", MediaType.APPLICATION_XML);
        requestSender.sendPostRequest(restMockEndpoint + "?param=paramValue2", "", MediaType.APPLICATION_XML);

        MockResponse requestUrlParams = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUEST_PARAMS);
        Document requestUrlParamsDoc = new DocumentImpl(requestUrlParams.getBody());

        assertThat(requestUrlParamsDoc,
            hasXPath("//recorded-request-params/recorded-request-param[1]", equalTo("param=paramValue1")));
        assertThat(requestUrlParamsDoc,
            hasXPath("//recorded-request-params/recorded-request-param[2]", equalTo("param=paramValue2")));
    }

    public void shouldVerifyRecordedRequestsWithHeaders(String restMockEndpoint) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader", "aValue");

        // Given - We send a post request to the end point with headers
        requestSender.sendPostRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.APPLICATION_XML, headers);

        //When we get the recorded request headers
        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS_HEADERS);

        // Then the header we sent is returned
        assertThat("Expected a response body", recordedRequests.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(recordedRequests.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

    public void shouldVerifyResourceIds(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        requestSender.sendPostRequest(restMockEndpoint + "/id1", "", MediaType.TEXT_XML);
        requestSender.sendPostRequest(restMockEndpoint + "/id2", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_RESOURCE_IDS);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[1]", equalTo("id1")));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[2]", equalTo("id2")));
    }

    public void shouldReturnCustomRESTPostResponseBodyAndDefaultResponseCode_WhilePassingResourceId(String restMockEndpoint)
        throws IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        doCustomResponseTestWithResourceId(restMockEndpoint, "someResourceId", "", HttpStatus.SC_CREATED);
    }

    public void shouldReturnCustomRESTPostResponseBodyAndSpecifiedResponseCode_WhilePassingResourceId(String restMockEndpoint)
        throws IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        doCustomResponseTestWithResourceId(restMockEndpoint, "otherResourceId", "?code=200", HttpStatus.SC_OK);

    }

    public void shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendPostRequest(restMockEndpoint + "/id123/id234", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE);
        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendPostRequest(restMockEndpoint + "/id123", "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS);
        assertThat("Expected HTTP 404 status code as resource paths are not enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldVerifyResourcePaths(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        String path1 = "id123/id234/id345";
        String path2 = "id567/id678/id789";
        requestSender.sendPostRequest(restMockEndpoint + "/" + path1, "", MediaType.TEXT_XML);
        requestSender.sendPostRequest(restMockEndpoint + "/" + path2, "", MediaType.TEXT_XML);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[1]", equalTo(path1)));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[2]", equalTo(path2)));
    }

    public void shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        String path = "id123/id234/id345";
        MockResponse mockResponse = requestSender.sendPostRequest(restMockEndpoint + "/" + path, "", MediaType.TEXT_XML);

        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", mockResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }
    
	public void shouldHandleRequestWithAttachment(String restMockEndpoint) throws ParserConfigurationException, SAXException, IOException {
        List<RestAttachment> attachments = new ArrayList<RestAttachment>();
        attachments.add(new RestAttachment(MediaType.APPLICATION_OCTET_STREAM, new FileReader().readBinaryFileContents(BINARY_ATTACHMENT_FILE_PATH)));
        
		requestSender.sendPostRequest(restMockEndpoint, "<dummyReq>dummyReqText1</dummyReq>", MediaType.APPLICATION_XML, Collections.EMPTY_MAP, attachments);

        MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/dummyReq[1]", equalTo("dummyReqText1")));
	}

    private void doCustomResponseTestWithResourceId(String restMockEndpoint, String resourceId, String customResponseParamString, int expectedStatusCode) throws IOException, ParserConfigurationException, SAXException {
        // not setting custom response code
        String customResponseXML = "<custom_post_response>custom REST POST response text</custom_post_response>";
        requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + customResponseParamString, customResponseXML, MediaType.TEXT_XML);

        // sending REST POST request
        MockResponse response = requestSender.sendPostRequest(restMockEndpoint + "/" + resourceId, "", MediaType.TEXT_XML);

        assertThat("default response code", response.getCode(), is(expectedStatusCode));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_post_response", equalTo("custom REST POST response text")));
    }
}
