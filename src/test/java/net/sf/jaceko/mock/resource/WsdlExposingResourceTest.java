package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.service.RequestExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class WsdlExposingResourceTest {

    @Mock
    private RequestExecutor service;

    private WsdlExposingResource resource = new WsdlExposingResource();

    @Before
    public void before() {
        initMocks(this);
        resource.setWebserviceMockService(service);
    }

    @Test
    public void shouldExposeWsdl() {
        String serviceName = "ticketing";
        String expectedWsdlString = "<dummy/>";

        when(service.getWsdl(serviceName)).thenReturn(expectedWsdlString);
        String wsdlStringResp = resource.getWsdl(serviceName);
        assertThat(wsdlStringResp, is(expectedWsdlString));

        serviceName = "mptu";
        expectedWsdlString = "<dummy2/>";
        when(service.getWsdl(serviceName)).thenReturn(expectedWsdlString);
        wsdlStringResp = resource.getWsdl(serviceName);
        assertThat(wsdlStringResp, is(expectedWsdlString));
    }


}
