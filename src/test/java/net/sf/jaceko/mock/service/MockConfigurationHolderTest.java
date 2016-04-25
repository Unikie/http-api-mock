package net.sf.jaceko.mock.service;

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import org.jboss.resteasy.spi.NotFoundException;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class MockConfigurationHolderTest {
    private MockConfigurationHolder configuration = new MockConfigurationHolder();

    @Test
    public void shouldReturnServices() {
        String name1 = "ticketing";
        String wsdlText1 = "<dummyWSDL/>";
        String name2 = "mptu";
        String wsdlText2 = "<dummyWSDL2/>";

        WebService service1 = new WebService(name1, wsdlText1);
        WebService service2 = new WebService(name2, wsdlText2);

        configuration.setWebServices(asList(service1, service2));

        assertThat(configuration.getWebServices(), hasItems(service1, service2));
    }

    @Test
    public void shouldReturnServiceByName() {
        String name1 = "ticketing";
        String wsdlText1 = "<dummyWSDL/>";
        String name2 = "mptu";
        String wsdlText2 = "<dummyWSDL2/>";

        WebService service1 = new WebService(name1, wsdlText1);
        WebService service2 = new WebService(name2, wsdlText2);

        configuration.setWebServices(asList(service1, service2));

        assertThat(configuration.getWebService(name2), is(service2));
        assertThat(configuration.getWebService(name1), is(service1));

    }

    @Test
    public void shouldReturnOperationByServiceNameAndRequestName() {
        String name1 = "ticketing";
        String wsdlText1 = "<dummyWSDL/>";
        String name2 = "mptu";
        String wsdlText2 = "<dummyWSDL2/>";

        WebService service1 = new WebService(name1, wsdlText1);
        WebService service2 = new WebService(name2, wsdlText2);

        String operationName1 = "reserveRequest";
        String operationName2 = "confirmRequest";

        WebserviceOperation operation1 = WebserviceOperation.name(operationName1).build();
        WebserviceOperation operation2 = WebserviceOperation.name(operationName2).build();

        String operationName3 = "prepayRequest";
        WebserviceOperation operation3 = WebserviceOperation.name(operationName3).build();

        service1.addOperation(0, operation1);
        service1.addOperation(1, operation2);

        service2.addOperation(0, operation3);

        configuration.setWebServices(asList(service1, service2));

        assertThat(configuration.getWebServiceOperation(name1, operationName1), is(operation1));
        assertThat(configuration.getWebServiceOperation(name1, operationName2), is(operation2));
        assertThat(configuration.getWebServiceOperation(name2, operationName3), is(operation3));

    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfServiceNotFound() {
        configuration.getWebServiceOperation("not_existing", "abc");
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfServiceNotFound2() {
        configuration.getWebService("not_existing");
    }


    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfWebserviceOperationNotFound() {
        String serviceName = "service1";
        String wsdlText1 = "<someWSDL/>";

        WebService service = new WebService(serviceName, wsdlText1);
        configuration.setWebServices(asList(service));

        configuration.getWebServiceOperation(serviceName, "not_existing_operation");

    }


}
