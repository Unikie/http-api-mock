package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.exception.ClientFaultException;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import net.sf.jaceko.mock.service.RequestExecutor;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class SoapEndpointResourceTest {
    private SoapEndpointResource resource = new SoapEndpointResource();

    @Mock
    private RequestExecutor service;
    
    @Mock
    private MockConfigurationHolder configurationHolder;

    private final MultivaluedMap<String, String> headers = new MultivaluedMapImpl<String, String>();

    @Mock
    private HttpHeaders mockHttpHeaders;

    @Mock
    private HttpServletRequest servletContext;

    @Before
    public void before() {
        initMocks(this);
        when(mockHttpHeaders.getRequestHeaders()).thenReturn(headers);
        resource.setWebserviceMockService(service);
        resource.setConfigurationHolder(configurationHolder);
    }

    @Test
    public void shouldParseRequestAndPassThroughInputMessageName() throws Exception {
        String serviceName = "ticketing";
        String inputMessageName = "dummyRequest";
        String request = soapRequest(inputMessageName);

        resource.performRequest(serviceName, servletContext, request, mockHttpHeaders);

        verify(service).performRequest(serviceName, inputMessageName, request, null, null, headers);

    }

    private String soapRequest(String inputMessageName) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">"
            + "<soapenv:Body><tem:"
            + inputMessageName
            + "></tem:"
            + inputMessageName
            + "></soapenv:Body></soapenv:Envelope>";
    }



    @Test
    public void shouldParseRequestAndPassThroughInputMessageNameWithHeaders() {
        String serviceName = "ticketing";
        String inputMessageName = "dummyRequest";
        String request = soapRequest(inputMessageName);

        headers.putSingle("someheader", "headervalue");
        headers.putSingle("someotherheader", "anotherheadervalue");

        resource.performRequest(serviceName, servletContext, request, mockHttpHeaders);

        verify(service).performRequest(serviceName, inputMessageName, request, null, null, headers);
    }

    @Test
    public void shouldParseAnotherRequestAndPassThroughInputMessageName() throws Exception {
        String serviceName = "ticketing";

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:book=\"http://www.bookmyshow.com/\">\r\n" +
            "   <soapenv:RecordedHeader/>\r\n" +
            "   <soapenv:Body>\r\n" +
            "      <book:objExecute>\r\n" +
            "         <book:strAppCode>TESTAPP</book:strAppCode>\r\n" +
            "	<book:strCommand>InitTrans</book:strCommand>\r\n" +
            "         <book:strVenueCode>THRA</book:strVenueCode>\r\n" +
            "         <book:lngTransactionIdentifier>23275</book:lngTransactionIdentifier>\r\n" +
            "      </book:objExecute>\r\n" +
            "   </soapenv:Body>\r\n" +
            "</soapenv:Envelope>";


        resource.performRequest(serviceName, servletContext, request, mockHttpHeaders);

        verify(service).performRequest(serviceName, "objExecute", request, null, null, headers);

    }



    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionInCaseOfMalformedRequestXML() throws Exception {
        String badXml = "<malformedXml>malformedXml>";
        resource.performRequest("ticketing", servletContext, badXml, mockHttpHeaders);

    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionInCaseOfEmptyRequest() throws Exception {
        String badXml = "";
        resource.performRequest("ticketing", servletContext, badXml, mockHttpHeaders);
    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionForImproperSoapRequest() throws Exception {
        String notASoapRequest = "<requestXML></requestXML>";
        resource.performRequest("ticketing", servletContext, notASoapRequest, mockHttpHeaders);

    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionForImproperSoapRequest2() throws Exception {
        String notASoapRequest = "<requestXML><aaa></aaa></requestXML>";
        resource.performRequest("ticketing", servletContext, notASoapRequest, mockHttpHeaders);

    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionForImproperSoapRequest3() throws Exception {
        String notASoapRequest = "<requestXML><aaa><bbb></bbb></aaa></requestXML>";
        resource.performRequest("ticketing", servletContext, notASoapRequest, mockHttpHeaders);

    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionForImproperSoapRequest4() throws Exception {
        String notASoapRequest = "<Envelope><aaa><bbb></bbb></aaa></Envelope>";
        resource.performRequest("ticketing", servletContext, notASoapRequest, mockHttpHeaders);

    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionInCaseOfEmptyBody() throws Exception {
        String notASoapRequest = "<Envelope><Body></Body></Envelope>";
        resource.performRequest("ticketing", servletContext, notASoapRequest, mockHttpHeaders);
    }

    @Test
    public void shouldReturnResponse() throws Exception {

        String serviceName = "ticketing";
        String request = soapRequest(serviceName);

        String responseBody = "<dummyResponse/>";
        int responseCode = 500;

        when(service.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseBody, responseCode));
        Response response = resource.performRequest(serviceName, servletContext, request, mockHttpHeaders);
        assertThat((String) response.getEntity(), is(responseBody));
        assertThat(response.getStatus(), is(responseCode));

    }

    @Test
    public void shouldReturnCustomHeader() {

        String serviceName = "ticketing";
        String request = soapRequest(serviceName);

        String responseBody = "<dummyResponse/>";
        int responseCode = 500;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("headerkey", "headervalue");

        when(service.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            MockResponse.body(responseBody).code(responseCode).headers(headers).build());

        Response response = resource.performRequest(serviceName, servletContext, request, mockHttpHeaders);
        assertThat(String.valueOf(response.getMetadata().getFirst("headerkey")), equalTo("headervalue"));
    }


}
