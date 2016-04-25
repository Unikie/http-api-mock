package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SoapMockIntegrationTest {

    private final static Logger log = Logger.getLogger(SoapMockIntegrationTest.class);

    // mocked endpoints configured in ws-mock.properties
    private static final String SOAP_MOCK_ENDPOINT = "http://localhost:8080/mock/services/SOAP/hello-soap/endpoint";
    private static final String SOAP_MOCK_BINARY_ENDPOINT = "http://localhost:8080/mock/services/SOAP/dummy-soap-binary/endpoint";

    private static final String SOAP_MOCK_BASE = "http://localhost:8080/mock/services/SOAP/hello-soap/operations";
    private static final String SOAP_MOCK_INIT = SOAP_MOCK_BASE + "/sayHello/init";
    private static final String SOAP_MOCK_RESPONSES = SOAP_MOCK_BASE + "/sayHello/responses";
    private static final String SOAP_MOCK_RECORDED_REQUESTS = SOAP_MOCK_BASE + "/sayHello/recorded-requests";
    private static final String SOAP_MOCK_POST_RECORDED_REQUESTS_HEADERS = SOAP_MOCK_BASE + "/sayHello/recorded-request-headers";
    private static final String SOAP_MOCK_POST_RECORDED_REQUEST_PARAMS = SOAP_MOCK_BASE + "/sayHello/recorded-request-params";

    private static final String SOAP_MOCK_BINARY_BASE = "http://localhost:8080/mock/services/SOAP/dummy-soap-binary/operations";
    private static final String SOAP_MOCK_BINARY_INIT = SOAP_MOCK_BINARY_BASE + "/dummySoapRequest/init";
    private static final String SOAP_MOCK_BINARY_RESPONSES = SOAP_MOCK_BINARY_BASE + "/dummySoapRequest/responses";
    private static final String SOAP_MOCK_BINARY_RECORDED_REQUESTS = SOAP_MOCK_BINARY_BASE + "/dummySoapRequest/recorded-requests";

    private static final String BINARY_DEFAULT_RESPONSE_FILE_NAME = "default_soap_multipart_response.dat";
    private static final String BINARY_DEFAULT_RESPONSE_FILE_PATH = "it/" + BINARY_DEFAULT_RESPONSE_FILE_NAME;

    private static final String REQUEST = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
        + "   <soapenv:Header/>\r\n"
        + "   <soapenv:Body>\r\n"
        + "      <urn:sayHello soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
        + "         <firstName xsi:type=\"xsd:string\">{0}</firstName>\r\n"
        + "      </urn:sayHello>\r\n"
        + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    private static final String REQUEST_WITH_WRONG_NAMESPACE = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
            + "   <soapenv:Header/>\r\n"
            + "   <soapenv:Body>\r\n"
            + "      <urn:sayHelloWithNamespace soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
            + "         <firstName xsi:type=\"xsd:string\">test</firstName>\r\n"
            + "      </urn:sayHelloWithNamespace>\r\n"
            + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    private static final String REQUEST_WITHOUT_NAMESPACE = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
            + "   <soapenv:Header/>\r\n"
            + "   <soapenv:Body>\r\n"
            + "      <sayHelloWithNamespace soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
            + "         <firstName xsi:type=\"xsd:string\">test</firstName>\r\n"
            + "      </sayHelloWithNamespace>\r\n"
            + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    private static final String REQUEST_WITH_NAMESPACE = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns123=\"http://test-ns.org/ns/testing\">\r\n"
            + "   <soapenv:Header/>\r\n"
            + "   <soapenv:Body>\r\n"
            + "      <ns123:sayHelloWithNamespace soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
            + "         <firstName xsi:type=\"xsd:string\">test</firstName>\r\n"
            + "      </ns123:sayHelloWithNamespace>\r\n"
            + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";


    private static final String RESPONSE_TEMPLATE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:urn=\"urn:examples:helloservice\" xmlns:soapenv=\"soapenv\">\r\n"
        + "   <soap:Body>\r\n"
        + "      <urn:sayHello soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
        + "         <greeting xsi:type=\"xsd:string\">{0}</greeting>\r\n"
        + "      </urn:sayHello>\r\n"
        + "   </soap:Body>\r\n" + "</soap:Envelope>";

    private static final String REQUEST_BINARY = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
            + "   <soapenv:Header/>\r\n"
            + "   <soapenv:Body>\r\n"
            + "      <dummySoapRequest soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
            + "      </dummySoapRequest>\r\n"
            + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    private HttpRequestSender requestSender = new HttpRequestSender();

    @Before
    public void initMock() throws IOException {
        // initalizing mock, clearing history of previous requests
        requestSender.sendPostRequest(SOAP_MOCK_INIT, "", MediaType.TEXT_XML);
    }

    @Test
    public void shouldReturnDefaultResponse() throws IOException, ParserConfigurationException,
        SAXException {

        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hello!!")));
    }

    @Test
    public void shouldReturnCustomResponseAndDefaultCode() throws IOException, ParserConfigurationException, SAXException {
        // setting up xml response body on mock
        String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Hi!");
        requestSender.sendPostRequest(SOAP_MOCK_RESPONSES, customResponseXML, MediaType.TEXT_XML);

        // sending SOAP request
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hi!")));
        assertThat("default response code", response.getCode(), is(200));

    }

    @Test
    public void shouldReturnCustomResponseAndCustomCode() throws IOException, ParserConfigurationException, SAXException {
        // setting up xml response body on mock
        String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Hola!");
        requestSender.sendPostRequest(SOAP_MOCK_RESPONSES + "?code=500", customResponseXML, MediaType.TEXT_XML);

        // sending SOAP request
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hola!")));

        assertThat("custom response code", response.getCode(), is(500));
    }

    @Test
    public void shouldReturnCustomSecondResponse() throws IOException,
        ParserConfigurationException, SAXException {
        // setting up xml response body on mock
        String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Aloha!");
        requestSender.sendPutRequest(SOAP_MOCK_RESPONSES + "/2", customResponseXML, MediaType.TEXT_XML);

        // sending 1st SOAP request
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);
        // sending 2nd SOAP request
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Aloha!")));
    }

    @Test
    public void shouldVerifyRecordedRequests() throws IOException,
        ParserConfigurationException, SAXException {
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Peter"), MediaType.TEXT_XML);

        MockResponse recordedRequests = requestSender.sendGetRequest(SOAP_MOCK_RECORDED_REQUESTS);
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/Envelope[1]/Body/sayHello/firstName", equalTo("Jacek")));

        assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/Envelope[2]/Body/sayHello/firstName", equalTo("Peter")));

    }

    @Test
    public void shouldVerifyRequestParameters() throws IOException, ParserConfigurationException,
        SAXException {
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT + "?param=paramValue1", MessageFormat.format(REQUEST, "Jan"), MediaType.TEXT_XML);
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT + "?param=paramValue2", MessageFormat.format(REQUEST, "Peter"), MediaType.TEXT_XML);

        MockResponse requestUrlParams = requestSender.sendGetRequest(SOAP_MOCK_POST_RECORDED_REQUEST_PARAMS);
        Document requestUrlParamsDoc = new DocumentImpl(requestUrlParams.getBody());

        assertThat(requestUrlParamsDoc,
            hasXPath("//recorded-request-params/recorded-request-param[1]", equalTo("param=paramValue1")));
        assertThat(requestUrlParamsDoc,
            hasXPath("//recorded-request-params/recorded-request-param[2]", equalTo("param=paramValue2")));
    }

    @Test
    public void shouldReturnCustomResponseWithHeader() throws Exception {
        requestSender.sendPostRequest(SOAP_MOCK_RESPONSES + "?headers=X-Signature:signatureValue", MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);

        assertThat("Expected X-Signature header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
    }

    @Test
    public void shouldReturnCustomResponseWithMultipleHeaders() throws Exception {
        requestSender.sendPostRequest(SOAP_MOCK_RESPONSES + "?headers=X-Signature:signatureValue,X-Date:tomorrow", MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);

        assertThat("Expected X-Signature header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Date"), equalTo("tomorrow"));
    }

    @Test
    public void shouldVerifyRecordedRequestsWithHeaders() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader", "aValue");

        // Given - We send a post request to the end point with headers
        requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML, headers);

        //When we get the recorded request headers
        MockResponse recordedRequestHeaders = requestSender.sendGetRequest(SOAP_MOCK_POST_RECORDED_REQUESTS_HEADERS);

        // Then the header we sent is returned
        assertThat("Expected a response body", recordedRequestHeaders.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequestHeaders.getBody());

        assertThat(recordedRequestHeaders.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

    @Test
    public void shouldFailWhenMessageHasNoNamespace() throws Exception {
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST_WITHOUT_NAMESPACE, MediaType.TEXT_XML);

        assertThat(response.getCode(), is(500));
        assertThat(response.getBody(), CoreMatchers.containsString("Message doesn't contain namespace"));
    }

    @Test
    public void shouldFailWhenMessageHasWrongNamespace() throws Exception {
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST_WITH_WRONG_NAMESPACE, MediaType.TEXT_XML);

        assertThat(response.getCode(), is(500));
        assertThat(response.getBody(), CoreMatchers.containsString("The namespace of message doesn't match"));
    }

    @Test
    public void shouldPassWithCorrectNamespace() throws Exception {
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST_WITH_NAMESPACE, MediaType.TEXT_XML);

        assertThat(response.getCode(), is(200));
        assertThat(response.getBody(), CoreMatchers.containsString("Hello!!"));
    }

    @Test
    public void shouldReturnBinaryResponse() throws Exception {
        MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_BINARY_ENDPOINT, REQUEST_BINARY, MediaType.APPLICATION_XML);

        // TODO does not validate the actual binary content. Should implement proper multipart handling?
        assertThat("Expected statuscode (200)", response.getCode(), is(200));
        assertThat("Content-Type is multipart/related", response.getHeaders().get("Content-Type"), containsString("multipart/related"));
        assertThat("Contains SOAP part", response.getBody(), containsString("<s:Envelope"));
        assertThat("Contains binary encoding part in body", response.getBody(), containsString("Content-Transfer-Encoding: binary"));
        assertThat("Contains octet-stream content type in body", response.getBody(), containsString("Content-Type: application/octet-stream"));
    }
}
