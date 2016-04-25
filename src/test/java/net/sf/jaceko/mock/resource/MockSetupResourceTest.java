package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockSetupExecutor;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MockSetupResourceTest {

    private BasicSetupResource resource = new RestServiceMockSetupResource();

    @Mock
    private MockSetupExecutor mockSetupExecutor;

    @Before
    public void before() {
        initMocks(this);
        resource.setMockSetupExecutor(mockSetupExecutor);
    }

    @Test
    public void shouldPassClearRecordedRequestsToServiceLayer() {
        String serviceName = "ticketing";
        String operationId = "reserveRequest";

        resource.initMock(serviceName, operationId);
        verify(mockSetupExecutor).initMock(serviceName, operationId);

        serviceName = "prepayService";
        operationId = "prepayRequest";

        resource.initMock(serviceName, operationId);
        verify(mockSetupExecutor).initMock(serviceName, operationId);

    }

    @Test
    public void initMockShouldReturnResponseWithStatusOK() {
        Response response = resource.initMock("", "");
        assertThat(response.getStatus(), is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldAddCustomResponse() {

        String serviceName = "ticketing";
        String operationId = "POST";
        String customResponseBody = "[]";
        int customResponseCode = 201;
        int delaySec = 1;
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getMediaType()).thenReturn(mediaType);

        resource.addResponse(httpHeaders, serviceName, operationId, customResponseCode, delaySec, null, customResponseBody);
        MockResponse expectedResponse = MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec)
            .contentType(mediaType).build();

        verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

    }

    @Test
    public void shouldAddCustomResponseWithHeader() {

        String serviceName = "ticketing";
        String operationId = "GET";
        String customResponseBody = "<dummyResponse>respTExt2</dummyResponse>";
        int customResponseCode = 200;
        int delaySec = 2;
        String headersToPrime = "headername:headervalue";
        HashMap<String, String> headersToPrimeMap = new HashMap<String, String>();
        headersToPrimeMap.put("headername", "headervalue");
        MediaType mediaType = MediaType.APPLICATION_XML_TYPE;

        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getMediaType()).thenReturn(mediaType);

        resource.addResponse(httpHeaders, serviceName, operationId, customResponseCode, delaySec, headersToPrime, customResponseBody);
        MockResponse expectedResponse = MockResponse.body(customResponseBody).code(customResponseCode).contentType(mediaType)
            .delaySec(delaySec).headers(headersToPrimeMap).build();

        verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

    }

    @Test
    public void shouldAddCustomResponseWithMultipleHeaders() {
        //given
        String serviceName = "ticketing";
        String operationId = "GET";
        String customResponseBody = "<dummyResponse>respTExt2</dummyResponse>";
        int customResponseCode = 200;
        int delaySec = 2;
        String headersToPrime = "headername:headervalue,someotherheader:anothervalue";

        HashMap<String, String> headersToPrimeMap = new HashMap<String, String>();
        headersToPrimeMap.put("headername", "headervalue");
        headersToPrimeMap.put("someotherheader", "anothervalue");

        MediaType mediaType = MediaType.APPLICATION_XML_TYPE;

        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getMediaType()).thenReturn(mediaType);

        //when
        resource.addResponse(httpHeaders, serviceName, operationId, customResponseCode, delaySec, headersToPrime, customResponseBody);

        MockResponse expectedResponse = MockResponse.body(customResponseBody).code(customResponseCode).contentType(mediaType)
            .delaySec(delaySec).headers(headersToPrimeMap).build();

        verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

    }

    @Test
    public void setResponseShouldReturnResponseWithStatusOK() {
        Response response = resource.setResponse(mock(HttpHeaders.class), "", "", 1, 0, 0, "", "");
        assertThat(response.getStatus(), is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldSetUpSecondResponse() {
        String serviceName = "ticketing";
        String operationId = "reserveRequest";
        int customResponseCode = 201;
        String headerString = "header1:value1";
        String customResponseBody = "<dummyResponse>aabb</dummyResponse>";
        int responseInOrder = 2;
        int delaySec = 2;
        MediaType mediaType = MediaType.APPLICATION_XML_TYPE;

        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getMediaType()).thenReturn(mediaType);

        resource.setResponse(httpHeaders, serviceName, operationId, responseInOrder, customResponseCode, delaySec, headerString,
            customResponseBody);
        verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
            MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec).contentType(mediaType)
                .headers(BasicSetupResource.parseHeadersToPrime(headerString)).build());

        serviceName = "prepayService";
        operationId = "prepayRequest";
        responseInOrder = 1;
        customResponseCode = 200;
        headerString = "header2:value2";
        delaySec = 5;
        mediaType = MediaType.APPLICATION_JSON_TYPE;

        when(httpHeaders.getMediaType()).thenReturn(mediaType);

        resource.setResponse(httpHeaders, serviceName, operationId, responseInOrder, customResponseCode, delaySec, headerString,
            customResponseBody);
        verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
            MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec).contentType(mediaType)
                .headers(BasicSetupResource.parseHeadersToPrime(headerString)).build());
    }

    @Test
    public void shouldSetUpFifthResponse() {
        String serviceName = "ticketing";
        String operationId = "reserveRequest";
        int customResponseCode = 200;
        String customResponseBody = "<dummyResponse>abc123</dummyResponse>";
        int responseInOrder = 5;
        String headerString = "header:value";

        resource.setResponse(mock(HttpHeaders.class), serviceName, operationId, responseInOrder, customResponseCode, 0, headerString,
            customResponseBody);
        verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
            MockResponse.body(customResponseBody).code(customResponseCode).headers(BasicSetupResource.parseHeadersToPrime(headerString)).build());

    }

}
