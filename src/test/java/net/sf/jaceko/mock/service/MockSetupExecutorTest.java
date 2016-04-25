package net.sf.jaceko.mock.service;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MockSetupExecutorTest {

    private static final String NOT_USED_RESOURCE_ID = null;
    private static final String NOT_USED_REQUEST_PARAM = null;

    @Mock
    private MockConfigurationHolder configurationHolder;

    private MockSetupExecutor setupExecutor = new MockSetupExecutor();

    private RecordedRequestsHolder recordedRequestsHolder;


    @Before
    public void init() {
        initMocks(this);
        setupExecutor.setMockserviceConfiguration(configurationHolder);
        recordedRequestsHolder = new RecordedRequestsHolder();
        recordedRequestsHolder.setMockserviceConfiguration(configurationHolder);
        setupExecutor.setRecordedRequestsHolder(recordedRequestsHolder);
        initWebService(anyString());
    }


    @Test
    public void shouldAddSeriesOfResponses() {
        String serviceName = "svc123";
        String operationId = "someRequest";
        MockResponse response1 = new MockResponse("<someResp/>");
        MockResponse response2 = new MockResponse("<someResp2/>");
        WebserviceOperation operation = new WebserviceOperation();

        when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
        setupExecutor.addCustomResponse(serviceName, operationId, response1);
        setupExecutor.addCustomResponse(serviceName, operationId, response2);

        assertThat(operation.getResponse(1), is(response1));
        assertThat(operation.getResponse(2), is(response2));

    }

    @Test
    public void shouldSetCustomResponse() {
        String serviceName = "mptu";
        String operationId = "prepayRequest";
        String expectedCustomResponse = "<dummyResp/>";
        WebserviceOperation operation = new WebserviceOperation();
        int requestInOrder = 2;

        when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
        setupExecutor.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse(expectedCustomResponse));
        MockResponse customResponse = operation.getResponse(requestInOrder);
        assertThat(customResponse.getBody(), is(expectedCustomResponse));

    }

    @Test
    public void shouldResetInvocationNumberWhileSettingUpRequest() {
        String serviceName = "mptu";
        String operationId = "prepayRequest";
        WebserviceOperation operation = new WebserviceOperation();
        int requestInOrder = 2;

        when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
        setupExecutor.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse("<dummyResp/>"));
        assertThat(operation.getNextInvocationNumber(), is(1));
        assertThat(operation.getNextInvocationNumber(), is(2));

        setupExecutor.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse("<dummyResp2/>"));
        assertThat(operation.getNextInvocationNumber(), is(1));

    }

    @Test
    public void shouldClearRecordedRequestsOnMockInit() {

        String request1 = "<req1/>";
        String serviceName = "mptu";
        String operationId = "prepayRequest";

        when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
        recordedRequestsHolder.recordRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID, null);

        Collection<String> recordedPrepayRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);
        assertThat(recordedPrepayRequests.size(), is(1));

        setupExecutor.initMock(serviceName, operationId);

        recordedPrepayRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);

        assertThat(recordedPrepayRequests.size(), is(0));
    }

    @Test
    public void shouldInitWebserviceOperationOnMockInit() {
        String serviceName = "mptu";
        String operationId = "prepayRequest";

        WebserviceOperation operation = mock(WebserviceOperation.class);

        when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
        setupExecutor.initMock(serviceName, operationId);
        verify(operation).init();


    }

    private void initWebService(String serviceName) {
        initWebService(serviceName, true);
    }

    private void initWebService(String serviceName, boolean ignoreXmlDeclaration) {
        WebService service = new WebService();
        service.setIgnoreXmlDeclaration(ignoreXmlDeclaration);
        when(configurationHolder.getWebService(serviceName)).thenReturn(service);
    }

}
