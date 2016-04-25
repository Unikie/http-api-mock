package net.sf.jaceko.mock.service;

import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;
import net.sf.jaceko.mock.matcher.OperationHavingDefaultResponseContentTypeEqualTo;
import net.sf.jaceko.mock.matcher.OperationHavingDefaultResponseEqualTo;
import net.sf.jaceko.mock.matcher.OperationHavingNameEqualTo;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.util.FileReader;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.MockitoAnnotations.initMocks;

public class PropertyProcessorTest {

    private PropertyProcessor propertyProcessor = new PropertyProcessor();

    @Mock
    private FileReader fileReader;

    @Before
    public void before() {
        initMocks(this);
        propertyProcessor.setFileReader(fileReader);
    }

    @Test
    public void shouldReturnCollectionOfOneRESTService() throws IOException {

        String serviceName = "somerestservice";
        String propertyString = "SERVICE[0].NAME=" + serviceName + "\r\n" + "SERVICE[0].TYPE=REST\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        assertThat(webServices.size(), is(1));
        assertThat(webServices, hasItem(new ServiceHavingNameEqualTo(serviceName)));
        assertThat(webServices, hasItem(new RestService()));

    }

    @Test
    public void shouldReturnCollectionOfOneSoapService() throws IOException {
        String serviceName = "ticketing";

        String propertyString = "SERVICE[0].NAME=" + serviceName + "\r\n" + "SERVICE[0].TYPE=SOAP\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        assertThat(webServices, hasItem(new ServiceHavingNameEqualTo(serviceName)));
        assertThat(webServices, hasItem(new SoapService()));

    }

    private Collection<WebService> processPropertiesAndReturnWebServices(String propertyString) throws IOException {
        Reader reader = new StringReader(propertyString);
        MockConfigurationHolder configuration = propertyProcessor.process(reader);
        return configuration.getWebServices();
    }

    @Test
    public void shouldReturnCollectionOfTwoServices() throws IOException {
        String serviceName1 = "ticketing";
        String serviceName2 = "mptu";
        String propertyString = "SERVICE[0].NAME=" + serviceName1 + "\r\n" + "SERVICE[1].NAME=" + serviceName2 + "\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        assertThat(webServices.size(), is(2));
        assertThat(webServices, hasItem(new ServiceHavingNameEqualTo(serviceName1)));
        assertThat(webServices, hasItem(new ServiceHavingNameEqualTo(serviceName2)));

    }

    @Test
    public void shouldReturnEmptyServicesColletion() throws IOException {
        String serviceName = "ticketing";
        String propertyString = "SERVICE_MaLformed[0].NAME=" + serviceName + "\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        assertThat(webServices.size(), is(0));

    }

    @Test
    public void shouldReturnServiceIndex() {
        String keyPart = "SERVICE[0]";
        assertThat(propertyProcessor.getServiceIndex(keyPart), is(0));

        keyPart = "SERVICE[1]";
        assertThat(propertyProcessor.getServiceIndex(keyPart), is(1));

        keyPart = "SERVICE[3]";
        assertThat(propertyProcessor.getServiceIndex(keyPart), is(3));

        keyPart = "SERVICE[10]";
        assertThat(propertyProcessor.getServiceIndex(keyPart), is(10));

    }

    @Test
    public void shouldReturnNegativeServiceIndex() {
        String malformed = "SERVICE";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "SERVICE[1";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "SERVICE[1aaaa";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "SERVICE[1]aaaa";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "aaaSERVICE[1]";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "SERVICE1]";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

        malformed = "SECE[1]";
        assertThat(propertyProcessor.getServiceIndex(malformed), is(-1));

    }

    @Test
    public void shouldReturnOperationIndex() {
        String keyPart = "OPERATION[0]";
        assertThat(propertyProcessor.getOperationIndex(keyPart), is(0));

        keyPart = "OPERATION[1]";
        assertThat(propertyProcessor.getOperationIndex(keyPart), is(1));

        keyPart = "OPERATION[3]";
        assertThat(propertyProcessor.getOperationIndex(keyPart), is(3));

        keyPart = "OPERATION[10]";
        assertThat(propertyProcessor.getOperationIndex(keyPart), is(10));

    }

    @Test
    public void shouldReturnNegativeOperationIndex() {
        String malformed = "OPERATION";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "OPERATION[1";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "OPERATION[1aaaa";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "OPERATION[1]aaaa";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "aaaOPERATION[1]";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "OPERATION1]";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

        malformed = "OPON[1]";
        assertThat(propertyProcessor.getOperationIndex(malformed), is(-1));

    }

    @Test
    public void shouldReturnPropertyIndex() {

    }

    @Test
    public void shouldReturnSoapServiceHavingOneOperation() throws IOException {
        String expectedInputMessageName = "reserveRequest";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].OPERATION[0].INPUT_MESSAGE="
            + expectedInputMessageName + "\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName)));
    }

    @Test
    public void shouldReturnServiceHavingOneOperation2() throws IOException {
        String expectedInputMessageName = "confirmRequest";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].OPERATION[0].INPUT_MESSAGE="
            + expectedInputMessageName + "\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName)));
    }

    @Test
    public void shouldReturnRestServiceHavingOneOperation() throws IOException {
        String expectedHttpMethod = "GET";
        String propertyString = "SERVICE[0].NAME=service1\r\n" + "SERVICE[0].OPERATION[0].HTTP_METHOD=" + expectedHttpMethod
            + "\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedHttpMethod)));

    }

    @Test(expected = ServiceNotConfiguredException.class)
    public void shouldThrowExceptionIfHTTpMethodNotRecognized() throws IOException {
        String badHttpMethod = "GOT";
        String propertyString = "SERVICE[0].NAME=service1\r\n" + "SERVICE[0].OPERATION[0].HTTP_METHOD=" + badHttpMethod + "\r\n";
        processPropertiesAndReturnWebServices(propertyString);

    }

    @Test
    public void shouldIgnoreUknownPropertiesOfService() throws IOException {
        String expectedInputMessageName = "confirmRequest";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].WSDL=ticketing.wsdl\r\n"
            + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=" + expectedInputMessageName + "\r\n"
            + "SERVICE[0].UNKNOWN_Property=unknow_prop_vaue\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName)));
    }

    @Test
    public void shouldReturnServiceHavingTwoOperations() throws IOException {
        String expectedInputMessageName = "confirmRequest";
        String expectedInputMessageName2 = "reserveRequest";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].WSDL=ticketing.wsdl\r\n"
            + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=" + expectedInputMessageName + "\r\n"
            + "SERVICE[0].OPERATION[1].INPUT_MESSAGE=" + expectedInputMessageName2 + "\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(2));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName)));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName2)));
    }

    @Test
    public void shouldIgnoreUknownPropertiesOfOperation() throws IOException {
        String expectedResponseFile = "confirmRequestResponse.xml";
        String expectedResponseFile2 = "reserveRequestResponse.xml";
        String expectedInputMessageName = "confirmRequest";
        String expectedInputMessageName2 = "reserveRequesT";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].WSDL=ticketing.wsdl\r\n"
            + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE=" + expectedResponseFile + "\r\n"
            + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=" + expectedInputMessageName + "\r\n"
            + "SERVICE[0].OPERATION[1].DEFAULT_RESPONSE=" + expectedResponseFile2 + "\r\n"
            + "SERVICE[0].OPERATION[1].INPUT_MESSAGE=" + expectedInputMessageName2 + "\r\n"
            + "SERVICE[0].OPERATION[1].UNKNOWN_PROP123=unknown_prop_value\r\n"
            + "SERVICE[0].OPERATION[0].UNKNOWN_PROP543=unknown_prop_value5634\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(2));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseEqualTo(expectedResponseFile)));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseEqualTo(expectedResponseFile2)));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName)));
        assertThat(operations, hasItem(new OperationHavingNameEqualTo(expectedInputMessageName2)));

    }

    @Test
    public void shouldReturnServiceHavingTwoOperationsHavingDefaultResponses() throws IOException {
        String expectedResponseFile = "confirmRequestResponse.xml";
        String expectedResponseFile2 = "reserveRequestResponse.xml";
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].WSDL=ticketing.wsdl\r\n"
            + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE=" + expectedResponseFile + "    \r\n"
            + "SERVICE[0].OPERATION[1].DEFAULT_RESPONSE=" + expectedResponseFile2 + "\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(2));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseEqualTo(expectedResponseFile)));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseEqualTo(expectedResponseFile2)));

    }

    @Test
    public void shouldReturnServiceHavingDefaultResponseCode() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE_CODE=204\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseCodeEqualTo(204)));

    }

    @Test
    public void shouldReturnTwoOperationsHavingDefaultResponseContentType() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n"
            + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE_CONTENT_TYPE=application/json\r\n"
            + "SERVICE[0].OPERATION[1].DEFAULT_RESPONSE_CONTENT_TYPE=application/xml\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(2));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseContentTypeEqualTo(MediaType.APPLICATION_JSON_TYPE)));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseContentTypeEqualTo(MediaType.APPLICATION_XML_TYPE)));

    }

    @Test
    public void shouldReturnTwoOperationsHavingDefaultResponseCodes() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE_CODE=200\r\n"
            + "SERVICE[0].OPERATION[1].DEFAULT_RESPONSE_CODE=201 \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(2));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseCodeEqualTo(200)));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseCodeEqualTo(201)));

    }

    @Test
    public void shouldReturnOperationsHavingDefaultResponseCodeSetTo200fNotSpecified() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].HTTP_METHOD=GET \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseCodeEqualTo(200)));
    }

    @Test
    public void shouldReturnOperationsHavingDefaultResponseContentSetToXMLIfNotSpecified() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].HTTP_METHOD=GET \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingDefaultResponseContentTypeEqualTo(MediaType.TEXT_XML_TYPE)));

    }

    @Test
    public void shouldReturnServiceHavingTrueIgnoreXmlDeclarationProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].IGNORE_XML_DECLARATION=true\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        boolean ignoreXmlDeclaration = service.isIgnoreXmlDeclaration();
        assertTrue(ignoreXmlDeclaration);
    }

    @Test
    public void shouldReturnServiceHavingFalseIgnoreXmlDeclarationProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].IGNORE_XML_DECLARATION=false\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        boolean ignoreXmlDeclaration = service.isIgnoreXmlDeclaration();
        assertFalse(ignoreXmlDeclaration);
    }

    @Test
    public void shouldReturnServiceHavingDefaultIgnoreXmlDeclarationProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=ticketing\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        WebService service = webServices.iterator().next();
        boolean ignoreXmlDeclaration = service.isIgnoreXmlDeclaration();
        assertTrue(ignoreXmlDeclaration);
    }

    @Test
    public void shouldReturnServiceHavingTrueEnableResourcePathsProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=pathAware\r\n" + "SERVICE[0].ENABLE_RESOURCE_PATHS=true\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        assertThat(webServices.size(), is(1));
        assertThat(webServices, hasItem(new ServiceHavingResourcePathsEnabledPropertyEqualTo(true)));
    }

    @Test
    public void shouldReturnServiceHavingFalseEnableResourcePathsProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=pathIgnorant\r\n" + "SERVICE[0].ENABLE_RESOURCE_PATHS=false\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        assertThat(webServices.size(), is(1));
        assertThat(webServices, hasItem(new ServiceHavingResourcePathsEnabledPropertyEqualTo(false)));
    }

    @Test
    public void shouldReturnServiceHavingDefaultEnableResourcePathsProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=pathIgnorant\r\n";
        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);

        assertThat(webServices.size(), is(1));
        assertThat(webServices, hasItem(new ServiceHavingResourcePathsEnabledPropertyEqualTo(false)));
    }

    @Test
    public void shouldReturnOperationsHavingBinaryPropertySpecifiedAsTrue() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].BINARY=true \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingBinaryPropertySetTo(true)));
    }

    @Test
    public void shouldReturnOperationsHavingBinaryPropertySpecifiedAsFalse() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].BINARY=false \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingBinaryPropertySetTo(false)));
    }

    @Test
    public void shouldReturnOperationsHavingDefaultBinaryProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=some_service\r\n" + "SERVICE[0].OPERATION[0].HTTP_METHOD=GET \r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHavingBinaryPropertySetTo(false)));
    }

    @Test
    public void shouldReturnOperationsHavingDefaultHeadersProperty() throws IOException {
        String propertyString = "SERVICE[0].NAME=service_with_headers\r\n" + "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE_HEADERS=";
        String expectedHeaders = "header1:headervalue1,header2:headervalue2";
        propertyString += expectedHeaders + "\r\n";

        Collection<WebService> webServices = processPropertiesAndReturnWebServices(propertyString);
        WebService service = webServices.iterator().next();
        Collection<WebserviceOperation> operations = service.getOperations();
        assertThat(operations.size(), is(1));
        assertThat(operations, hasItem(new OperationHeadersSetTo(expectedHeaders)));
    }

    private static final class ServiceHavingNameEqualTo extends ArgumentMatcher<WebService> {
        private WebService service;
        private final String name;

        @Override
        public boolean matches(Object argument) {
            service = (WebService) argument;
            return name.equals(service.getName());
        }

        ServiceHavingNameEqualTo(String name) {
            super();
            this.name = name;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("name should be " + name + " but is " + service);

        }

    }

    private static final class SoapService extends ArgumentMatcher<WebService> {
        private WebService service;

        @Override
        public boolean matches(Object argument) {
            service = (WebService) argument;
            return ServiceType.SOAP.equals(service.getServiceType());

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("service type: SOAP but is " + service.getServiceType());

        }

    }

    private static final class RestService extends ArgumentMatcher<WebService> {
        private WebService service;

        @Override
        public boolean matches(Object argument) {
            service = (WebService) argument;
            return ServiceType.REST.equals(service.getServiceType());

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("service type: REST but is " + service.getServiceType());

        }

    }

    private static final class OperationHavingDefaultResponseCodeEqualTo extends ArgumentMatcher<WebserviceOperation> {
        private WebserviceOperation operation;
        private final int defaultResponseCode;

        OperationHavingDefaultResponseCodeEqualTo(int defaultResponseCode) {
            super();
            this.defaultResponseCode = defaultResponseCode;
        }

        @Override
        public boolean matches(Object argument) {
            operation = (WebserviceOperation) argument;
            return defaultResponseCode == operation.getDefaultResponseCode();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("operation having defaultResponseCode = " + defaultResponseCode
                + " but is operation having defaultResponseCode = " + operation.getDefaultResponseCode());
        }

    }

    private static final class ServiceHavingResourcePathsEnabledPropertyEqualTo extends ArgumentMatcher<WebService> {
        private final boolean value;
        private WebService service;

        ServiceHavingResourcePathsEnabledPropertyEqualTo(boolean value) {
            this.value = value;
        }

        @Override
        public boolean matches(Object argument) {
            service = (WebService) argument;
            return value == service.isEnableResourcePaths();
        }


        @Override
        public void describeTo(Description description) {
            description.appendText("enableResourcePaths should be " + value + " but is " + service.isEnableResourcePaths());

        }

    }

    private static final class OperationHavingBinaryPropertySetTo extends ArgumentMatcher<WebserviceOperation> {
        private WebserviceOperation operation;
        private final boolean binaryProperty;

        OperationHavingBinaryPropertySetTo(boolean binaryProperty) {
            this.binaryProperty = binaryProperty;
        }

        @Override
        public boolean matches(Object argument) {
            operation = (WebserviceOperation) argument;
            return binaryProperty == operation.isBinary();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("operation having binaryProperty = " + binaryProperty
                    + " but is operation having binaryProperty = " + operation.isBinary());
        }

    }

    private static final class OperationHeadersSetTo extends ArgumentMatcher<WebserviceOperation> {
        private WebserviceOperation operation;
        private String headers;

        OperationHeadersSetTo(String headers) {
            this.headers = headers;
        }

        @Override
        public boolean matches(Object o) {
            operation = (WebserviceOperation) o;

            String[] operationHeaders = parseAndSortHeaders(operation.getDefaultResponseHeaders());
            String[] expectedHeaders = parseAndSortHeaders(headers);

            return Arrays.equals(operationHeaders, expectedHeaders);
        }

        private String[] parseAndSortHeaders(String headerString) {
            String[] result = headerString.split(",");
            Arrays.sort(result);

            return result;
        }

        @Override
        public void describeTo(Description desc) {
            desc.appendText("operation has headers set to: " + parseAndSortHeaders(operation.getDefaultResponseHeaders())
            + " but it should be: " + parseAndSortHeaders(headers));
        }
    }
}
