package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dto.*;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import org.jboss.resteasy.spi.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ServicesResourceTest {
    private ServicesResource resource = new ServicesResource();

    @Mock
    private MockConfigurationHolder mockConfigurationService;

    @Mock
    private HttpServletRequest context;

    @Before
    public void before() {
        initMocks(this);
        resource.setMockConfigurationService(mockConfigurationService);
    }

    @Test
    public void shouldReturnWebServicesDtoContainingSoapService() throws URISyntaxException {
        WebService service = new WebService();
        service.setServiceType(ServiceType.SOAP);
        service.setName("dummyService1");

        Collection<WebService> servicesCollection = asList(service);
        when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
        when(context.getServerName()).thenReturn("someserver");
        when(context.getServerPort()).thenReturn(1234);
        when(context.getContextPath()).thenReturn("/mock");


        WebServicesDto expectedWebServicesDto = new WebServicesDto();
        WebServiceDto webServiceDto1 = new WebServiceDto();
        webServiceDto1.setName("dummyService1");
        webServiceDto1.setType(ServiceType.SOAP);
        webServiceDto1.setWsdlUri("http://someserver:1234/mock/services/SOAP/dummyService1/wsdl");
        webServiceDto1.setEndpointUri("http://someserver:1234/mock/services/SOAP/dummyService1/endpoint");
        expectedWebServicesDto.getWebservicesList().add(webServiceDto1);

        assertThat(resource.getWebServices(context), is(expectedWebServicesDto));
    }

    @Test
    public void shouldReturnWebServicesDtoContainingRestService() throws URISyntaxException {
        WebService service = new WebService();
        service.setServiceType(ServiceType.REST);
        service.setName("dummyService2");

        Collection<WebService> servicesCollection = asList(service);
        when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(5678);
        when(context.getContextPath()).thenReturn("/mock");

        WebServicesDto expectedWebServicesDto = new WebServicesDto();
        WebServiceDto webServiceDto = new WebServiceDto();
        webServiceDto.setName("dummyService2");
        webServiceDto.setType(ServiceType.REST);
        webServiceDto.setEndpointUri("http://server:5678/mock/services/REST/dummyService2/endpoint");
        webServiceDto.setMultipartEndpointUri("http://server:5678/mock/services/REST/dummyService2/multipart-endpoint");
        expectedWebServicesDto.getWebservicesList().add(webServiceDto);

        assertThat(resource.getWebServices(context), is(expectedWebServicesDto));
    }

    @Test
    public void shouldReturnEmptyWebservicesDtoObject() {
        when(mockConfigurationService.getWebServices()).thenReturn(Collections.<WebService>emptyList());
        assertThat(resource.getWebServices(context), is(new WebServicesDto()));
    }

    @Test
    public void shouldReturn3Services() {

        WebService service1 = new WebService();
        service1.setName("dummyService1");

        WebService service2 = new WebService();
        service2.setName("dummyService2");

        WebService service3 = new WebService();
        service3.setName("dummyService3");

        Collection<WebService> servicesCollection = asList(service1, service2, service3);
        when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);

        assertThat(resource.getWebServices(context).getWebservicesList().size(), is(3));
        assertThat(resource.getWebServices(context).getWebservicesList().get(0).getName(), is("dummyService1"));
        assertThat(resource.getWebServices(context).getWebservicesList().get(1).getName(), is("dummyService2"));
        assertThat(resource.getWebServices(context).getWebservicesList().get(2).getName(), is("dummyService3"));

    }

    @Test
    public void shouldReturnWebserviceWithOperationRefernces() {
        WebService service = new WebService();
        service.setServiceType(ServiceType.SOAP);
        service.setName("dummyService");
        WebserviceOperation operation = new WebserviceOperation();
        operation.setOperationName("dummyRequest");
        service.addOperation(0, operation);
        WebserviceOperation operation2 = new WebserviceOperation();
        operation2.setOperationName("otherRequest");
        service.addOperation(1, operation2);

        Collection<WebService> servicesCollection = asList(service);
        when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
        when(context.getServerName()).thenReturn("localhost");
        when(context.getServerPort()).thenReturn(9876);
        when(context.getContextPath()).thenReturn("/mock");

        OperationRefDto operationRef = new OperationRefDto();
        operationRef.setName("dummyRequest");
        operationRef.setUri("http://localhost:9876/mock/services/SOAP/dummyService/operations/dummyRequest");

        OperationRefDto operationRef2 = new OperationRefDto();
        operationRef2.setName("otherRequest");
        operationRef2.setUri("http://localhost:9876/mock/services/SOAP/dummyService/operations/otherRequest");

        assertThat(resource.getWebServices(context).getWebservicesList().get(0).getOperationRefs(),
            contains(operationRef, operationRef2));

    }

    @Test
    public void shouldReturnSOAPOperationDtoContainingName() {
        WebserviceOperation operation = new WebserviceOperation();
        operation.setOperationName("dummyRequest");

        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(operation);

        OperationDto operationDto = resource.getOperation("SOAP", "", "", context);
        assertThat(operationDto.getName(), is("dummyRequest"));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaining4SetupResourceRefs() {

        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());

        OperationDto operationDto = resource.getOperation("SOAP", "", "", context);
        assertThat(operationDto.getSetupResources(), hasSize(4));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaingInitResourceRef() {
        when(mockConfigurationService.getWebServiceOperation("someService", "someRequest")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto initResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/SOAP/someService/operations/someRequest/init", HttpMethod.POST,
            "operation initialization");

        OperationDto operationDto = resource.getOperation("SOAP", "someService", "someRequest", context);
        assertThat(operationDto.getSetupResources(), hasItem(initResourceRef));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaingAddResponseResourceRef() {
        when(mockConfigurationService.getWebServiceOperation("someService", "someRequest")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto postResponseResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/SOAP/someService/operations/someRequest/responses", HttpMethod.POST,
            "add custom response");

        OperationDto operationDto = resource.getOperation("SOAP", "someService", "someRequest", context);
        assertThat(operationDto.getSetupResources(), hasItem(postResponseResourceRef));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaingSet1stResponseResourceRef() {
        when(mockConfigurationService.getWebServiceOperation("someService", "someRequest")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto set1stResponseResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/SOAP/someService/operations/someRequest/responses/1", HttpMethod.PUT,
            "set first custom response");

        OperationDto operationDto = resource.getOperation("SOAP", "someService", "someRequest", context);
        assertThat(operationDto.getSetupResources(), hasItem(set1stResponseResourceRef));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaingSet2ndResponseResourceRef() {
        WebserviceOperation operation = new WebserviceOperation();

        when(mockConfigurationService.getWebServiceOperation("someService", "someRequest")).thenReturn(operation);
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto set1stResponseResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/SOAP/someService/operations/someRequest/responses/2", HttpMethod.PUT,
            "set second custom response");

        OperationDto operationDto = resource.getOperation("SOAP", "someService", "someRequest", context);
        assertThat(operationDto.getSetupResources(), hasItem(set1stResponseResourceRef));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContainingThreeVerificationResourceRef() {

        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());

        OperationDto operationDto = resource.getOperation("SOAP", "", "", context);
        assertThat(operationDto.getVerificationResources(), hasSize(3));
    }

    @Test
    public void shouldReturnSOAPOperationDtoContaingRequestVerificationResourceRef() {
        WebserviceOperation operation = new WebserviceOperation();

        when(mockConfigurationService.getWebServiceOperation("someService", "someRequest")).thenReturn(operation);
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/SOAP/someService/operations/someRequest/recorded-requests", HttpMethod.GET,
            "recorded requests");

        OperationDto operationDto = resource.getOperation("SOAP", "someService", "someRequest", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfServiceTypeUnknow() {
        OperationDto operationDto = resource.getOperation("SOAP123", "someService", "someRequest", context);
        assertThat(operationDto, nullValue());
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContaining4SetupResourceRefs() {
        when(mockConfigurationService.getWebService(anyString())).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());

        OperationDto operationDto = resource.getOperation("REST", "", "GET", context);
        assertThat(operationDto.getSetupResources(), hasSize(4));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingInitResourceRef() {
        when(mockConfigurationService.getWebService("service123")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("service123", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("serverName");
        when(context.getServerPort()).thenReturn(9191);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto initResourceRef = new ResourceRefDto(
            "http://serverName:9191/mock/services/REST/service123/operations/GET/init", HttpMethod.POST,
            "operation initialization");

        OperationDto operationDto = resource.getOperation("REST", "service123", "GET", context);
        assertThat(operationDto.getSetupResources(), hasItem(initResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingAddResponseResourceRef() {
        when(mockConfigurationService.getWebService("otherService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("otherService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("localhost");
        when(context.getServerPort()).thenReturn(2134);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto postResponseResourceRef = new ResourceRefDto(
            "http://localhost:2134/mock/services/REST/otherService/operations/GET/responses", HttpMethod.POST,
            "add custom response");

        OperationDto operationDto = resource.getOperation("REST", "otherService", "GET", context);
        assertThat(operationDto.getSetupResources(), hasItem(postResponseResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingSet1stResponseResourceRef() {
        when(mockConfigurationService.getWebService("svcName")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("svcName", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto set1stResponseResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/REST/svcName/operations/GET/responses/1", HttpMethod.PUT,
            "set first custom response");

        OperationDto operationDto = resource.getOperation("REST", "svcName", "GET", context);
        assertThat(operationDto.getSetupResources(), hasItem(set1stResponseResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingSet2ndResponseResourceRef() {
        when(mockConfigurationService.getWebService("svcName")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("svcName", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(9090);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto set1stResponseResourceRef = new ResourceRefDto(
            "http://server:9090/mock/services/REST/svcName/operations/GET/responses/2", HttpMethod.PUT,
            "set second custom response");

        OperationDto operationDto = resource.getOperation("REST", "svcName", "GET", context);
        assertThat(operationDto.getSetupResources(), hasItem(set1stResponseResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingRequestVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/GET/recorded-requests", HttpMethod.GET,
            "recorded requests");

        OperationDto operationDto = resource.getOperation("REST", "someService", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContaingResourceIdsVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/GET/recorded-resource-ids", HttpMethod.GET,
            "recorded resource ids");

        OperationDto operationDto = resource.getOperation("REST", "someService", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingResourcePathsVerificationResourceRef_WhenResourcePathsEnabled() {
        WebService service = new WebService();
        service.setEnableResourcePaths(true);
        when(mockConfigurationService.getWebService("pathEnabledService")).thenReturn(service);
        when(mockConfigurationService.getWebServiceOperation("pathEnabledService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedPathsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/pathEnabledService/operations/GET/recorded-resource-paths", HttpMethod.GET,
            "recorded resource paths");

        OperationDto operationDto = resource.getOperation("REST", "pathEnabledService", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedPathsResourceRef));
    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingRequestParamsVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/GET/recorded-request-params", HttpMethod.GET,
            "recorded request parameters");

        OperationDto operationDto = resource.getOperation("REST", "someService", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));

    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContainingRequestHeadersVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "GET")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/GET/recorded-request-headers", HttpMethod.GET,
            "recorded request headers");

        OperationDto operationDto = resource.getOperation("REST", "someService", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));

    }

    @Test
    public void shouldReturnREST_GET_OperationDtoContaining4VerificationResourceRefs() {
        when(mockConfigurationService.getWebService(anyString())).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());

        OperationDto operationDto = resource.getOperation("REST", "any", "GET", context);
        assertThat(operationDto.getVerificationResources(), hasSize(4));
    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContaining4VerificationResourceRefs() {
        when(mockConfigurationService.getWebService(anyString())).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());

        OperationDto operationDto = resource.getOperation("REST", "any", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasSize(4));
    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContainingRequestVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "POST")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/POST/recorded-requests", HttpMethod.GET,
            "recorded requests");

        OperationDto operationDto = resource.getOperation("REST", "someService", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));
    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContainingRequestParamsVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "POST")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/POST/recorded-request-params", HttpMethod.GET,
            "recorded request parameters");

        OperationDto operationDto = resource.getOperation("REST", "someService", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));

    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContainingRequestHeadersVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "POST")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/POST/recorded-request-headers", HttpMethod.GET,
            "recorded request headers");

        OperationDto operationDto = resource.getOperation("REST", "someService", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));

    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContainingResourceIdsVerificationResourceRef() {
        when(mockConfigurationService.getWebService("someService")).thenReturn(new WebService());
        when(mockConfigurationService.getWebServiceOperation("someService", "POST")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/someService/operations/POST/recorded-resource-ids", HttpMethod.GET,
            "recorded resource ids");

        OperationDto operationDto = resource.getOperation("REST", "someService", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedRequestsResourceRef));
    }

    @Test
    public void shouldReturnREST_POST_OperationDtoContainingResourceIdsVerificationResourceRef_WhenResourcePathsEnabled() {
        WebService service = new WebService();
        service.setEnableResourcePaths(true);
        when(mockConfigurationService.getWebService("pathEnabledService")).thenReturn(service);
        when(mockConfigurationService.getWebServiceOperation("pathEnabledService", "POST")).thenReturn(new WebserviceOperation());
        when(context.getServerName()).thenReturn("server");
        when(context.getServerPort()).thenReturn(8888);
        when(context.getContextPath()).thenReturn("/mock");

        ResourceRefDto recordedPathsResourceRef = new ResourceRefDto(
            "http://server:8888/mock/services/REST/pathEnabledService/operations/POST/recorded-resource-paths", HttpMethod.GET,
            "recorded resource paths");

        OperationDto operationDto = resource.getOperation("REST", "pathEnabledService", "POST", context);
        assertThat(operationDto.getVerificationResources(), hasItem(recordedPathsResourceRef));
    }
}
