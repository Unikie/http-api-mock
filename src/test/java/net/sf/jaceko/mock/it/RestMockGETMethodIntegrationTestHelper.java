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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Created by esa on 12.1.2015.
 */
public class RestMockGETMethodIntegrationTestHelper {
    private static final String REST_MOCK_GET_INIT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/init";
    private static final String REST_MOCK_GET_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/responses";
    private static final String REST_MOCK_GET_RESPONSE_1 = REST_MOCK_GET_RESPONSES + "/1";
    private static final String REST_MOCK_GET_RESPONSE_2 = REST_MOCK_GET_RESPONSES + "/2";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-resource-paths";
    private static final String REST_MOCK_GET_RECORDED_REQUEST_PARAMS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-request-params";
    private static final String REST_MOCK_GET_RECORDED_REQUESTS_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-request-headers";

    private static final String REST_MOCK_GET_INIT_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/GET/init";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/GET/recorded-resource-ids";
    private static final String REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE = "http://localhost:8080/mock/services/REST/dummy-rest-paths-enabled/operations/GET/recorded-resource-paths";
    private static final String BINARY_DEFAULT_RESPONSE_FILE_NAME = "default_rest_get_binary_response.gif";
    private static final String BINARY_DEFAULT_RESPONSE_FILE_PATH = "it/" + BINARY_DEFAULT_RESPONSE_FILE_NAME;

    private final HttpRequestSender requestSender = new HttpRequestSender();

    public void initMock() throws IOException {
        // initalizing mock, clearing history of previous requests
        requestSender.sendPostRequest(REST_MOCK_GET_INIT, "", MediaType.TEXT_XML);
        requestSender.sendPostRequest(REST_MOCK_GET_INIT_FOR_PATH_ENABLED_SERVICE, "", MediaType.TEXT_XML);
    }

    // default response defined in ws-mock.properties
    public void shouldReturnDefaultRESTGetResponse(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        assertThat(response.getContentType(), is("application/vnd.specific+xml"));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//get_response_data", equalTo("default REST GET response text")));

    }

    public void shouldReturnDefaultRESTGetResponse2(String forbiddenRestMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        MockResponse response = requestSender.sendGetRequest(forbiddenRestMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));

    }

    public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        String customResponseXML = "<custom_get_response>custom REST GET response text</custom_get_response>";
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        assertThat(response.getContentType(), is(MediaType.TEXT_XML_TYPE.toString()));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_get_response", equalTo("custom REST GET response text")));

        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
    }

    public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode_WhilePassingResourceId(String restMockEndpoint)
        throws IOException, ParserConfigurationException, SAXException {
        // setting up response body on mock
        // not setting custom response code
        String customResponseXML = "<custom_get_response>custom REST GET response text</custom_get_response>";
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint + "/someResourceId");
        assertThat(response.getContentType(), is(MediaType.TEXT_XML_TYPE.toString()));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc,
            hasXPath("//custom_get_response", equalTo("custom REST GET response text")));

        assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
    }

    public void shouldReturnCustomRESTGetResponseBodyAndCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        String customResponseXML = "<custom_get_response>not authorized</custom_get_response>";
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?code=403", customResponseXML, MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat("custom response body", serviceResponseDoc, hasXPath("//custom_get_response", equalTo("not authorized")));

        assertThat("custom response code", response.getCode(), is(HttpStatus.SC_FORBIDDEN));
    }


    public void shouldReturnCustomRESTGetResponseCode(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?code=401", null, MediaType.APPLICATION_JSON);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        assertThat("custom response code", response.getCode(), is(HttpStatus.SC_UNAUTHORIZED));

    }


    public void shouldReturnConsecutiveCustomRESTGetResponses(String restMockEndpoint) throws
        IOException, ParserConfigurationException, SAXException {
        // setting up consecutive responses on mock
        String customResponseXML1 = "<custom_get_response>custom REST GET response text 1</custom_get_response>";
        requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/1" + "?code=200", customResponseXML1, MediaType.TEXT_XML);

        String customResponseXML2 = "<custom_get_response>custom REST GET response text 2</custom_get_response>";
        requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/2" + "?code=403", customResponseXML2, MediaType.TEXT_XML);

        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//custom_get_response", equalTo("custom REST GET response text 1")));

        response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
        serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//custom_get_response", equalTo("custom REST GET response text 2")));
    }


    public void shouldReturnDefaultResponseCode(String restMockEndpoint) throws IOException,
        ParserConfigurationException, SAXException {
        // setting up 1st response on mock, without response code
        requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/1", "", MediaType.TEXT_XML);

        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat(response.getCode(), is(HttpStatus.SC_OK)); // default
        // response code
        // defined in
        // ws-mock.properties

    }


    public void shouldVerifyRequestParameters(String restMockEndpoint) throws IOException, ParserConfigurationException,
        SAXException {
        requestSender.sendGetRequest(restMockEndpoint + "?param=paramValue1");
        requestSender.sendGetRequest(restMockEndpoint + "?param=paramValue2");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_REQUEST_PARAMS);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc,
            hasXPath("//recorded-request-params/recorded-request-param[1]", equalTo("param=paramValue1")));
        assertThat(verifyResponseDoc,
            hasXPath("//recorded-request-params/recorded-request-param[2]", equalTo("param=paramValue2")));

    }


    public void shouldVerifyResourceIds(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        requestSender.sendGetRequest(restMockEndpoint + "/id123");
        requestSender.sendGetRequest(restMockEndpoint + "/id567");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[1]", equalTo("id123")));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[2]", equalTo("id567")));
    }


    public void shouldReturnCustomResponseWithHeader(String restMockEndpoint) throws Exception {
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?headers=X-Signature:signatureValue", "<body/>", MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
    }

    public void shouldReturnCustomResponseWithMultipleHeaders(String restMockEndpoint) throws Exception {
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?headers=X-Signature:signatureValue,X-Date:tomorrow", "<body/>", MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);

        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Date"), equalTo("tomorrow"));
    }

    public void shouldReturnCustomResponseHeadersSetToSpecificResponses(String restMockEndpoint) throws Exception {
        requestSender.sendPutRequest(REST_MOCK_GET_RESPONSE_1 + "?headers=FirstHeader:FirstValue", "<body/>", MediaType.TEXT_XML);
        requestSender.sendPutRequest(REST_MOCK_GET_RESPONSE_2 + "?headers=SecondHeader:SecondValue", "<body/>", MediaType.TEXT_XML);

        MockResponse response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat("Expected FirstHeader header to be returned from mock", response.getHeader("FirstHeader"), equalTo("FirstValue"));

        response = requestSender.sendGetRequest(restMockEndpoint);
        assertThat("Expected SecondHeader header to be returned from mock", response.getHeader("SecondHeader"), equalTo("SecondValue"));
    }


    public void shouldVerifyRecordedRequestsWithHeaders(String restMockEndpoint) throws Exception {
        // Given we've sent a get request with headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader", "aValue");
        requestSender.sendGetRequest(restMockEndpoint, headers);

        //When we get the recorded headers
        MockResponse recordedRequestsHeaders = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_REQUESTS_HEADERS);

        //Then the header sent in the Get request is returned
        assertThat("Expected a response body", recordedRequestsHeaders.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequestsHeaders.getBody());

        assertThat(recordedRequestsHeaders.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

    public void shouldReturnHttp404ForGetRequestedResourceIds_WhenResourcePathsEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendGetRequest(restMockEndpoint + "/id123/id234");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS_FOR_PATH_ENABLED_SERVICE);
        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldReturnHttp404ForGetRequestedResourcePaths_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        requestSender.sendGetRequest(restMockEndpoint + "/id123");

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS);
        assertThat("Expected HTTP 404 status code as resource paths are not enabled for service", verifyResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldVerifyResourcePaths(String restMockEndpoint) throws IOException, ParserConfigurationException, SAXException {
        String path1 = "id123/id234/id345";
        String path2 = "id567/id678/id789";
        requestSender.sendGetRequest(restMockEndpoint + "/" + path1);
        requestSender.sendGetRequest(restMockEndpoint + "/" + path2);

        MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_PATHS_FOR_PATH_ENABLED_SERVICE);
        Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[1]", equalTo(path1)));
        assertThat(verifyResponseDoc, hasXPath("//recorded-resource-paths/recorded-resource-path[2]", equalTo(path2)));
    }

    public void shouldReturnHttp404ForRequestWithResourcePath_WhenResourcePathsNotEnabled(String restMockEndpoint) throws IOException {
        String path = "id123/id234/id345";
        MockResponse mockResponse = requestSender.sendGetRequest(restMockEndpoint + "/" + path);

        assertThat("Expected HTTP 404 status code as resource paths are enabled for service", mockResponse.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    public void shouldReturnBinaryResponseForBinaryEnabledOperation(String restMockEndpoint) throws IOException {
        MockResponse mockResponse = requestSender.sendGetRequest(restMockEndpoint, true);

        assertThat("Expected binary content in response", mockResponse.getBinaryBody(), is(new FileReader().readBinaryFileContents(BINARY_DEFAULT_RESPONSE_FILE_PATH)));
        assertThat("Expected file name in Content-Disposition header", mockResponse.getHeader("Content-Disposition"), is("attachment; filename=" + BINARY_DEFAULT_RESPONSE_FILE_NAME));
    }

    public void shouldReturnCorrectHeadersForOperationWithHeadersSet(String restMockEndpoint) throws IOException {
        MockResponse mockResponse = requestSender.sendGetRequest(restMockEndpoint);

        assertThat("Expected header 'Header1' is present in the response", mockResponse.getHeader("Header1"), is("Value1"));
        assertThat("Expected header 'Header-2' is present in the response", mockResponse.getHeader("Header-2"), is("header_value_2"));
    }
}
