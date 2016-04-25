package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.MessageFormat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Interation tests of the services resource returning information about configured webservice mocks.
 * Check the ws-mock.properties file for mock configuration
 * <p/>
 * To run tests in eclipse start server typing executing mvn jetty:run
 */
public class ServicesResourceIntegrationTest {
    private static final String SERVICES = "http://localhost:8080/mock/services";

    private static final String OPERATIONS = "http://localhost:8080/mock/services/{0}/{1}/operations/{2}";
    HttpRequestSender requestSender = new HttpRequestSender();

    @Test
    public void shouldReturnServicesInformation() throws IOException, ParserConfigurationException,
        SAXException {
        MockResponse response = requestSender.sendGetRequest(SERVICES);
        assertThat("Response status code (200)", response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat("hello-soap", serviceResponseDoc, hasXPath("//services/service[@name='hello-soap']"));
        assertThat("hello-soap-withwdsl", serviceResponseDoc, hasXPath("//services/service[@name='hello-soap-withwsdl']"));
        assertThat("dummy-rest", serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']"));
        assertThat("dummy-rest-notauthorized", serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest-notauthorized']"));
        assertThat("dummy-rest-paths-enabled", serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest-paths-enabled']"));

        assertThat("Number of soap services", serviceResponseDoc, hasXPath("count(//services/service[@type='SOAP'])", equalTo("4")));
        assertThat("Number of rest services", serviceResponseDoc, hasXPath("count(//services/service[@type='REST'])", equalTo("6")));

        assertThat("sayHello is found", serviceResponseDoc,
            hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@name", equalTo("sayHello")));
        assertThat("sayHello matches ",
            serviceResponseDoc,
            hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@uri",
                equalTo("http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello")));

        assertRestServiceWithAllOperations(serviceResponseDoc, "dummy-rest");
        assertRestServiceWithAllOperations(serviceResponseDoc, "dummy-rest-paths-enabled");
    }

    @Test
    public void shouldReturnSOAPOperationDetails() throws IOException, ParserConfigurationException,
        SAXException {
        String serviceType = "SOAP";
        String serviceName = "hello-soap";
        String operationName = "sayHello";
        MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
            operationName));
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//operation[@name='sayHello']"));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/init']"));
        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses']"));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses/1']"));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses/2']"));
        assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/recorded-requests']"));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/verification-resources/resource-ref[@description='recorded request headers' and @http-method='GET' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/recorded-request-headers']"));

        assertThat(
            serviceResponseDoc,
            hasXPath("//operation/verification-resources/resource-ref[@description='recorded request parameters' and @http-method='GET' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/recorded-request-params']"));

        assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("3")));


    }

    @Test
    public void shouldReturnREST_GET_OperationDetails() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest", "GET", false);
    }

    @Test
    public void shouldReturnREST_PUT_OperationDetails() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest", "PUT", false);
    }

    @Test
    public void shouldReturnREST_DELETE_OperationDetails() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest", "DELETE", false);
    }

    @Test
    public void shouldReturnREST_POST_OperationDetails() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest", "POST", false);
    }

    @Test
    public void shouldReturnREST_GET_OperationDetails_WhenResourcePathsEnabled() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest-paths-enabled", "GET", true);
    }

    @Test
    public void shouldReturnREST_PUT_OperationDetails_WhenResourcePathsEnabled() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest-paths-enabled", "PUT", true);
    }

    @Test
    public void shouldReturnREST_DELETE_OperationDetails_WhenResourcePathsEnabled() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest-paths-enabled", "DELETE", true);
    }

    @Test
    public void shouldReturnREST_POST_OperationDetails_WhenResourcePathsEnabled() throws IOException,
        ParserConfigurationException, SAXException {
        testRestOperationDetails("dummy-rest-paths-enabled", "POST", true);
    }

    @Test
    public void shouldReturn404IfWrongOperationType() throws IOException {
        String serviceName = "hello-soap";
        String operationName = "sayHello";
        MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP123", serviceName,
            operationName));
        assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void shouldReturn404IfServiceNotFound() throws IOException {
        String serviceName = "not-existing";
        String operationName = "sayHello";
        MockResponse response = requestSender
            .sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP", serviceName, operationName));
        assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));

    }

    @Test
    public void shouldReturn404IfOperationNotFound() throws IOException {
        String serviceName = "hello-soap";
        String operationName = "sayHello";
        MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP123", serviceName,
            operationName));
        assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    private void assertRestServiceWithAllOperations(Document serviceResponseDoc, String serviceName) {
        assertThat(serviceResponseDoc, hasXPath(restOperationXPath(serviceName, "GET")));
        assertThat(serviceResponseDoc, hasXPath(restOperationXPath(serviceName, "PUT")));
        assertThat(serviceResponseDoc, hasXPath(restOperationXPath(serviceName, "POST")));
        assertThat(serviceResponseDoc, hasXPath(restOperationXPath(serviceName, "DELETE")));
    }

    private void testRestOperationDetails(String serviceName, String operationName, boolean resourcePathsEnabled) throws IOException, ParserConfigurationException, SAXException {
        String serviceType = "REST";
        MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
            operationName));
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc, hasXPath("//operation[@name='" + operationName + "']"));
        assertRestOperationSetupResources(serviceResponseDoc, serviceName, operationName);
        assertRestOperationVerificationResources(serviceName, operationName, serviceResponseDoc, resourcePathsEnabled);
    }

    private void assertRestOperationSetupResources(Document serviceResponseDoc, String serviceName, String operationName) {
        assertThat(serviceResponseDoc, hasXPath("count(//operation/" + OperationResourceType.SETUP_RESOURCE.getPath() + "/resource-ref)", equalTo("4")));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "init", "operation initialization", OperationResourceType.SETUP_RESOURCE, HttpMethod.POST)));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "responses", "add custom response", OperationResourceType.SETUP_RESOURCE, HttpMethod.POST)));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "responses/1", "set first custom response", OperationResourceType.SETUP_RESOURCE, HttpMethod.PUT)));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "responses/2", "set second custom response", OperationResourceType.SETUP_RESOURCE, HttpMethod.PUT)));
    }

    private void assertRestOperationVerificationResources(String serviceName, String operationName, Document serviceResponseDoc, boolean resourcePathsEnabled) {
        assertThat(serviceResponseDoc, hasXPath("count(//operation/" + OperationResourceType.VERIFICATION_RESOURCE.getPath() + "/resource-ref)", equalTo("4")));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "recorded-requests", "recorded requests", OperationResourceType.VERIFICATION_RESOURCE, HttpMethod.GET)));
        if (resourcePathsEnabled) {
            assertThat(
                serviceResponseDoc,
                hasXPath(restOperationResourceXPath(serviceName, operationName, "recorded-resource-paths", "recorded resource paths", OperationResourceType.VERIFICATION_RESOURCE, HttpMethod.GET)));
        } else {
            assertThat(
                serviceResponseDoc,
                hasXPath(restOperationResourceXPath(serviceName, operationName, "recorded-resource-ids", "recorded resource ids", OperationResourceType.VERIFICATION_RESOURCE, HttpMethod.GET)));
        }
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "recorded-request-params", "recorded request parameters", OperationResourceType.VERIFICATION_RESOURCE, HttpMethod.GET)));
        assertThat(
            serviceResponseDoc,
            hasXPath(restOperationResourceXPath(serviceName, operationName, "recorded-request-headers", "recorded request headers", OperationResourceType.VERIFICATION_RESOURCE, HttpMethod.GET)));
    }

    private String restOperationXPath(String mockServiceName, String operationName) {
        return new StringBuilder().append("//services/service[@name='").append(mockServiceName).append("']/operations/operation-ref[@name='")
            .append(operationName).append("' and @uri='http://localhost:8080/mock/services/REST/").append(mockServiceName)
            .append("/operations/").append(operationName).append("']").toString();
    }

    private String restOperationResourceXPath(String mockServiceName, String mockOperationName, String resourceOperationName, String resourceOperationDescription,
                                              OperationResourceType resourceType, HttpMethod httpMethod) {


        return new StringBuilder().append("//operation/").append(resourceType.getPath()).append("/resource-ref[@description='").append(resourceOperationDescription)
            .append("' and @http-method='").append(httpMethod).append("' and @uri='http://localhost:8080/mock/services/REST/")
            .append(mockServiceName).append("/operations/").append(mockOperationName).append("/").append(resourceOperationName).append("']").toString();
    }

    private enum OperationResourceType {
        SETUP_RESOURCE("setup-resources"), VERIFICATION_RESOURCE("verification-resources");
        private final String path;

        OperationResourceType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
