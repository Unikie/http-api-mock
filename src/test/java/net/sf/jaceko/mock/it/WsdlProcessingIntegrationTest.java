package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Integration tests checking if WSDL files have been properly parsed and mock
 * operations have been added to SOAP mocks
 * <p/>
 * Check the ws-mock.properties file for mock configuration
 * <p/>
 * To run tests in eclipse start server typing executing mvn jetty:run
 */
public class WsdlProcessingIntegrationTest {

    // mocked endpoints configured in ws-mock.properties
    private static final String SERVICES = "http://localhost:8080/mock/services";

    private static final String HELLO_MOCK_ENDPOINT = "http://localhost:8080/mock/services/SOAP/hello-soap-withwsdl/endpoint";

    private static final String HELLO_REQUEST = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
        + "   <soapenv:Header/>\r\n"
        + "   <soapenv:Body>\r\n"
        + "      <urn:sayHello soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
        + "         <firstName xsi:type=\"xsd:string\">{0}</firstName>\r\n"
        + "      </urn:sayHello>\r\n"
        + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    private static final String CONVERSION_RATE_ENDPOINT = "http://localhost:8080/mock/services/SOAP/webservicex-rate-convertor/endpoint";
    private static final String CONVERSION_RATE_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.webserviceX.NET/\">\r\n"
        + "   <soapenv:Header/>\r\n"
        + "   <soapenv:Body>\r\n"
        + "      <web:ConversionRate>\r\n"
        + "         <web:FromCurrency>CHF</web:FromCurrency>\r\n"
        + "         <web:ToCurrency>PLN</web:ToCurrency>\r\n"
        + "      </web:ConversionRate>\r\n" + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    HttpRequestSender requestSender = new HttpRequestSender();

    // hello-soap-withwsdl service mock, using hello.wsdl
    @Test
    public void shouldReturnSayHelloOperationFetchedFromWsdlFile() throws ClientProtocolException, IOException,
        ParserConfigurationException, SAXException {
        MockResponse response = requestSender.sendGetRequest(SERVICES);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(serviceResponseDoc,
            hasXPath("//services/service[@name='hello-soap-withwsdl']/operations/operation-ref/@name", equalTo("sayHello")));

    }

    // webservicex-rate-convertor service mock, using
    // webservicex-rate-convertor.wsdl
    @Test
    public void shouldReturnConversionRateOperationFetchedFromWsdlFile() throws ClientProtocolException, IOException,
        ParserConfigurationException, SAXException {
        MockResponse response = requestSender.sendGetRequest(SERVICES);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));
        Document serviceResponseDoc = new DocumentImpl(response.getBody());

        assertThat(
            serviceResponseDoc,
            hasXPath("//services/service[@name='webservicex-rate-convertor']/operations/operation-ref/@name",
                equalTo("ConversionRate")));

    }

    @Test
    public void shouldReturnDefaultConversionRateResponseGeneratedFromWsdlFile() throws UnsupportedEncodingException,
        ClientProtocolException, IOException, ParserConfigurationException, SAXException {
        MockResponse response = requestSender.sendPostRequest(CONVERSION_RATE_ENDPOINT, CONVERSION_RATE_REQUEST,
            MediaType.TEXT_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/ConversionRateResponse/ConversionRateResult"));

    }

    @Test
    public void shouldReturnDefaultHelloResponseGeneratedFromWsdlFile() throws ClientProtocolException, IOException,
        ParserConfigurationException, SAXException {

        MockResponse response = requestSender.sendPostRequest(HELLO_MOCK_ENDPOINT, HELLO_REQUEST, MediaType.TEXT_XML);
        assertThat(response.getCode(), is(HttpStatus.SC_OK));

        Document serviceResponseDoc = new DocumentImpl(response.getBody());
        assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHelloResponse/greeting"));
    }

}
