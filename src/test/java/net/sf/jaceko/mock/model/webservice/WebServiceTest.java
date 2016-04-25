package net.sf.jaceko.mock.model.webservice;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebServiceTest {

    private WebService service;

    @Before
    public void init() {
        service = new WebService();
    }

    @Test
    public void shouldGetOperationByIndx() {
        WebserviceOperation operation = new WebserviceOperation();
        int indx = 1;
        service.addOperation(indx, operation);
        assertThat(service.getOperation(indx), is(operation));
    }

    @Test
    public void shouldGetOperationByName() {
        WebserviceOperation operation1 = WebserviceOperation.name("name12").build();
        service.addOperation(1, operation1);
        WebserviceOperation operation2 = WebserviceOperation.name("name34").build();
        service.addOperation(2, operation2);

        assertThat(service.getOperation("name12"), is(operation1));
        assertThat(service.getOperation("name34"), is(operation2));
        assertThat(service.getOperation("noyExisting"), nullValue());
    }

}
