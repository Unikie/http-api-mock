package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import net.sf.jaceko.mock.service.RequestExecutor;
import org.apache.commons.httpclient.HttpStatus;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RestEndpointResourceTest {

    private static final String NOT_USED_RESPONSE_BODY = "";

    private static final MockResponse NOT_USED_RESPONSE = new MockResponse();

    private static final String NOT_USED_RESOURCE_ID = null;

    private static final String SERVICE_NAME = "bms_refdata";

    private RestEndpointResource resource = new RestEndpointResource();

    @Mock
    private RequestExecutor requestExecutor;

    @Mock
    private HttpServletRequest servletContext;

    @Mock
    private HttpHeaders mockHttpHeaders;

    @Mock
    private WebService webService;

    private MockConfigurationHolder configurationHolder;

    private final MultivaluedMap<String, String> headers = new MultivaluedMapImpl<String, String>();

    @Before
    public void before() {
        initMocks(this);
        when(webService.getName()).thenReturn(SERVICE_NAME);
        configurationHolder = new MockConfigurationHolder();
        configurationHolder.setWebServices(Arrays.asList(new WebService[] { webService }));
        resource.setMockConfigurationHolder(configurationHolder);
        resource.setWebserviceMockService(requestExecutor);
        when(mockHttpHeaders.getRequestHeaders()).thenReturn(headers);
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            NOT_USED_RESPONSE);
    }

    @Test
    public void shouldPerformGetRequest() {
        String urlParams = "msg=abc";
        when(servletContext.getQueryString()).thenReturn(urlParams);
        resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        verify(requestExecutor).performRequest(SERVICE_NAME, "GET", NOT_USED_RESPONSE_BODY, urlParams, null, headers);
    }

    @Test
    public void shouldPerformGetRequestWithHeaders() {
        String urlParams = "msg=abc";

        headers.putSingle("someheader", "headervalue");
        headers.putSingle("someotherheader", "anotherheadervalue");


        when(servletContext.getQueryString()).thenReturn(urlParams);

        resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);

        verify(requestExecutor).performRequest(SERVICE_NAME, "GET", NOT_USED_RESPONSE_BODY, urlParams, null, headers);
    }

    @Test
    public void shouldPerformGetRequestPassingResourceId() {
        String resourceId = "resId12";
        String urlParams = "msg=def";
        when(servletContext.getQueryString()).thenReturn(urlParams);
        resource.performGetRequest(SERVICE_NAME, servletContext, resourceId, mockHttpHeaders);
        verify(requestExecutor).performRequest(SERVICE_NAME, "GET", NOT_USED_RESPONSE_BODY, urlParams, resourceId, headers);
    }

    @Test
    public void shouldReturnGET_OKResponse() {
        String responseReturnedByServiceLayer = "someResponseText";
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_OK;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));
        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
    }

    @Test
    public void shouldReturnGET_FORBIDDEN_Response() {
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_FORBIDDEN;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(null, responseCodeReturnedByServiceLayer));
        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
    }

    @Test
    public void shouldReturnGET_OKResponseOnRequestWith_RESOURCE_ID() {
        String responseReturnedByServiceLayer = "someResponseText";
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_OK;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));

        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, NOT_USED_RESOURCE_ID, mockHttpHeaders);
        assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));

    }

    @Test
    public void shouldReturnGET_FORBIDDEN_ResponseOnRequestWith_RESOURCE_ID() {
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_FORBIDDEN;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(null, responseCodeReturnedByServiceLayer));
        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, NOT_USED_RESOURCE_ID, mockHttpHeaders);
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
    }

    @Test
    public void shouldReturnJsonContentType() {
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            MockResponse.body(NOT_USED_RESPONSE_BODY).contentType(APPLICATION_JSON_TYPE).build());

        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        assertThat(getResponse.getMetadata().getFirst("Content-Type").toString(), is("application/json"));

    }

    @Test
    public void shouldReturnXMLContentType() {
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            MockResponse.body(NOT_USED_RESPONSE_BODY).contentType(MediaType.TEXT_XML_TYPE).build());

        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        assertThat(getResponse.getMetadata().getFirst("Content-Type").toString(), is("text/xml"));

    }

    @Test
    public void shouldPerformPostRequest() {
        String urlParams = "msg=abc";
        String request = "<dummyRequest>abc</dummyRequest>";

        when(servletContext.getQueryString()).thenReturn(urlParams);

        resource.performPostRequest(SERVICE_NAME, servletContext, mockHttpHeaders, request);

        verify(requestExecutor).performRequest(SERVICE_NAME, "POST", request, urlParams, null, headers);
    }

    @Test
    public void shouldPerformPostRequestWithHeaders() {
        String urlParams = "msg=abc";
        String request = "<dummyRequest>abc</dummyRequest>";

        when(servletContext.getQueryString()).thenReturn(urlParams);

        headers.putSingle("someheader", "headervalue");
        headers.putSingle("someotherheader", "anotherheadervalue");

        resource.performPostRequest(SERVICE_NAME, servletContext, mockHttpHeaders, request);

        verify(requestExecutor).performRequest(SERVICE_NAME, "POST", request, urlParams, null, headers);
    }

    @Test
    public void shouldReturnPOST_CREATEDResponse() {
        String responseReturnedByServiceLayer = "someResponse";
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_CREATED;

        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));

        Response response = resource.performPostRequest(SERVICE_NAME, servletContext, mockHttpHeaders, null);

        assertThat((String) response.getEntity(), is(responseReturnedByServiceLayer));
        assertThat(response.getStatus(), is(responseCodeReturnedByServiceLayer));

    }

    @Test
    public void shouldPerformPutRequest() {
        String request = "<dummyRequest>def</dummyRequest>";
        resource.performPutRequest(SERVICE_NAME, mockHttpHeaders, request);
        verify(requestExecutor).performRequest(SERVICE_NAME, "PUT", request, null, null, headers);
    }

    @Test
    public void shouldPerformPutRequestWithHeaders() {
        String request = "<dummyRequest>def</dummyRequest>";

        headers.putSingle("someheader", "headervalue");
        headers.putSingle("someotherheader", "anotherheadervalue");

        resource.performPutRequest(SERVICE_NAME, mockHttpHeaders, request);

        verify(requestExecutor).performRequest(SERVICE_NAME, "PUT", request, null, null, headers);
    }

    @Test
    public void shouldPerformPutRequestPassingResourceId() {
        String resourceId = "resId12";
        String request = "<dummyRequest>abc</dummyRequest>";
        resource.performPutRequest(SERVICE_NAME, resourceId, mockHttpHeaders, request);
        verify(requestExecutor).performRequest(SERVICE_NAME, "PUT", request, null, resourceId, headers);
    }

    @Test
    public void shouldReturnPUT_CONFLICTResponse() {
        String responseReturnedByServiceLayer = "someResponse123";
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, 409));
        Response response = resource.performPutRequest(SERVICE_NAME, mockHttpHeaders, null);
        assertThat((String) response.getEntity(), is(responseReturnedByServiceLayer));

    }

    @Test
    public void shouldPerformDeleteRequest() {
        resource.performDeleteRequest(SERVICE_NAME, mockHttpHeaders);
        verify(requestExecutor).performRequest(SERVICE_NAME, "DELETE", NOT_USED_RESPONSE_BODY, null, null, headers);
    }

    @Test
    public void shouldPerformDeleteRequestWithHeaders() {
        headers.putSingle("someheader", "headervalue");
        headers.putSingle("someotherheader", "anotherheadervalue");

        resource.performDeleteRequest(SERVICE_NAME, mockHttpHeaders);

        verify(requestExecutor).performRequest(SERVICE_NAME, "DELETE", NOT_USED_RESPONSE_BODY, null, null, headers);
    }

    @Test
    public void shouldPerformDeleteRequestPassingResourceId() {
        String resourceId = "resId12";
        resource.performDeleteRequest(SERVICE_NAME, resourceId, mockHttpHeaders);
        verify(requestExecutor).performRequest(SERVICE_NAME, "DELETE", NOT_USED_RESPONSE_BODY, null, resourceId, headers);
    }

    @Test
    public void shouldReturnDELETE_NO_CONTENTResponse() {
        String responseReturnedByServiceLayer = "someResponseText";
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_NO_CONTENT;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));
        Response getResponse = resource.performDeleteRequest(SERVICE_NAME, mockHttpHeaders);
        assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
    }

    @Test
    public void shouldReturnDELETE_NO_CONTENTResponseOnRequestWith_RESOURCE_ID() {
        String responseReturnedByServiceLayer = "someResponseText";
        int responseCodeReturnedByServiceLayer = HttpStatus.SC_NO_CONTENT;
        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));

        Response getResponse = resource.performDeleteRequest(SERVICE_NAME, NOT_USED_RESOURCE_ID, mockHttpHeaders);
        assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
        assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));

    }

    @Test
    public void shouldReturnCustomHeader() {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("headerkey", "headervalue");

        when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(
            MockResponse.body(NOT_USED_RESPONSE_BODY).headers(headers).build());

        Response getResponse = resource.performGetRequest(SERVICE_NAME, servletContext, mockHttpHeaders);
        assertThat(String.valueOf(getResponse.getMetadata().getFirst("headerkey")), equalTo("headervalue"));
    }


}
