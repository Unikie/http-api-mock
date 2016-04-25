package net.sf.jaceko.mock.model.webservice;

import net.sf.jaceko.mock.exception.ClientFaultException;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static javax.ws.rs.core.MediaType.TEXT_XML_TYPE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebserviceOperationTest {

    private WebserviceOperation operation;

    @Before
    public void before() {
        operation = new WebserviceOperation();
    }

    @Test
    public void shouldSetupCustom1stResponse() {
        operation.setCustomResponse(MockResponse.body("adsadsa").contentType(TEXT_XML_TYPE).build(), 1);
        assertThat(operation.getResponse(1).getBody(), is("adsadsa"));
        assertThat(operation.getResponse(1).getContentType(), is(TEXT_XML_TYPE.toString()));
    }

    @Test
    public void shouldSetupCustomResponseTwice() {
        operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
        MockResponse secondCustomResponse = new MockResponse("sadfsdsdf2");
        operation.setCustomResponse(secondCustomResponse, 1);
        assertThat(operation.getResponse(1), is(secondCustomResponse));
    }

    @Test
    public void shouldReturnDefaultIf2ndResponseNotDefined() {
        operation.setDefaultResponseText("defaultResp");
        operation.setDefaultResponseCode(HttpStatus.SC_CREATED);

        operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);

        assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
        assertThat(operation.getResponse(2).getCode(), is(HttpStatus.SC_CREATED));
    }

    @Test
    public void shouldReturnDefaultResponse() {
        operation.setDefaultResponseText("defaultResp");
        operation.setDefaultResponseCode(HttpStatus.SC_OK);

        operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
        operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);

        assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
        assertThat(operation.getResponse(2).getCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldReturnDefaultResponseCodeIfCodeNodeDefinedInCustomResponse() {
        operation.setDefaultResponseCode(HttpStatus.SC_OK);

        int code = 0;
        operation.setCustomResponse(new MockResponse("dummy", code), 1);
        assertThat(operation.getResponse(1).getCode(), is(HttpStatus.SC_OK));

    }

    @Test
    public void shouldSetupCustom2ndResponse() {
        MockResponse customResponse = new MockResponse("adsadsa", 201);
        operation.setCustomResponse(customResponse, 2);
        assertThat(operation.getResponse(2), is(customResponse));
    }

    @Test
    public void shouldAddSeriesOfResponses() {
        MockResponse customResponse1 = new MockResponse("abc1", 201);
        MockResponse customResponse2 = new MockResponse("def45", 200);
        MockResponse customResponse3 = new MockResponse("sadf1", 403);
        operation.addCustomResponse(customResponse1);
        operation.addCustomResponse(customResponse2);
        operation.addCustomResponse(customResponse3);

        assertThat(operation.getResponse(1), is(customResponse1));
        assertThat(operation.getResponse(2), is(customResponse2));
        assertThat(operation.getResponse(3), is(customResponse3));

    }

    @Test
    public void shouldSetConsecutiveResponses() {
        MockResponse customResponse1 = new MockResponse("sadfsadfsa1", 201);
        MockResponse customResponse2 = new MockResponse("sadfsadfsa2", 200);
        operation.setCustomResponse(customResponse2, 2);
        operation.setCustomResponse(customResponse1, 1);

        assertThat(operation.getResponse(1), is(customResponse1));
        assertThat(operation.getResponse(2), is(customResponse2));
    }

    @Test
    public void shouldReturnDefaultResponseOn1stCallAndCustomOn2nd() {
        operation.setDefaultResponseText("defaultResp");
        operation.setCustomResponse(new MockResponse("sadfsadfsa2"), 2);

        assertThat(operation.getResponse(1).getBody(), is("defaultResp"));
        assertThat(operation.getResponse(2).getBody(), is("sadfsadfsa2"));

    }

    @Test
    public void shouldClearDefaultResponses() {
        operation.setDefaultResponseText("defaultResp");
        operation.setCustomResponse(new MockResponse("customResp123"), 1);
        operation.setCustomResponse(new MockResponse("customResp567"), 2);
        assertThat(operation.getResponse(1).getBody(), is("customResp123"));
        assertThat(operation.getResponse(2).getBody(), is("customResp567"));
        operation.init();
        assertThat(operation.getResponse(1).getBody(), is("defaultResp"));
        assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
    }

    @Test
    public void shouldClearInvocationCount() {
        assertThat(operation.getNextInvocationNumber(), is(1));
        assertThat(operation.getNextInvocationNumber(), is(2));
        assertThat(operation.getNextInvocationNumber(), is(3));

        operation.init();
        assertThat(operation.getNextInvocationNumber(), is(1));
        assertThat(operation.getNextInvocationNumber(), is(2));
        assertThat(operation.getNextInvocationNumber(), is(3));

    }

    @Test
    public void shouldGetNextInvocationCount() {
        assertThat(operation.getNextInvocationNumber(), is(1));
        assertThat(operation.getNextInvocationNumber(), is(2));
        assertThat(operation.getNextInvocationNumber(), is(3));
        assertThat(operation.getNextInvocationNumber(), is(4));
    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionInCaseOfNameSpaceMissing() throws Exception {
        operation.setRequest(soapRequestWithoutNamespace());
        operation.setNameSpaces("http://non-valid.org/namespace");
        operation.getResponse(1);
    }

    @Test(expected = ClientFaultException.class)
    public void shouldThrowExceptionInCaseOfNameSpaceNotMatch() throws Exception {
        operation.setRequest(soapRequestWithNamespace());
        operation.setNameSpaces("http://non-valid.org/namespace");
        operation.getResponse(1);
    }

    @Test
    public void shouldParseRequestAndPassThroughRequestWithNamespaces() {
        String bodyString = "This is the default response :)";
        operation.setRequest(soapRequestWithTwoNamespace());
        operation.setNameSpaces("http://mystes.com/ns/library,http://mystes.com/ns/person");
        operation.setDefaultResponseText(bodyString);
        operation.setDefaultResponseCode(200);

        MockResponse response = operation.getResponse(1);

        assertThat(response.getBody(), is(bodyString));
        assertThat(response.getCode(), is(200));
    }


    private String soapRequestWithoutNamespace() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n>" +
                "   <soapenv:RecordedHeader/>\r\n" +
                "   <soapenv:Body>\r\n" +
                "      <objExecute>\r\n" +
                "      <strAppCode>TESTAPP</strAppCode>\r\n" +
                "	   <strCommand>InitTrans</strCommand>\r\n" +
                "      <strVenueCode>THRA</strVenueCode>\r\n" +
                "      <lngTransactionIdentifier>23275</lngTransactionIdentifier>\r\n" +
                "      </objExecute>\r\n" +
                "   </soapenv:Body>\r\n" +
                "</soapenv:Envelope>";
    }

    private String soapRequestWithNamespace() {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:book=\"http://www.bookmyshow.com/\">\r\n" +
                "   <soapenv:RecordedHeader/>\r\n" +
                "   <soapenv:Body>\r\n" +
                "      <book:objExecute>\r\n" +
                "      <book:strAppCode>TESTAPP</book:strAppCode>\r\n" +
                "	   <book:strCommand>InitTrans</book:strCommand>\r\n" +
                "      <book:strVenueCode>THRA</book:strVenueCode>\r\n" +
                "      <book:lngTransactionIdentifier>23275</book:lngTransactionIdentifier>\r\n" +
                "      </book:objExecute>\r\n" +
                "   </soapenv:Body>\r\n" +
                "</soapenv:Envelope>";
    }

    private String soapRequestWithTwoNamespace() {
        return "<?xml version=\"1.0\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Body>"
                + "<lib:library xmlns:lib=\"http://mystes.com/ns/library\" xmlns:hr=\"http://mystes.com/ns/person\">"
                + "<lib:book id=\"b083621746\" available=\"true\">"
                + "<lib:isbn>0836217462</lib:isbn>"
                + "<lib:title>Being a Dog Is a Full-Time Job</lib:title>"
                + "<hr:author id=\"CMS\">"
                + "<hr:name>Charles M Schulz</hr:name>"
                + "<hr:born>1922-11-26</hr:born>"
                + "<hr:dead>2000-02-12</hr:dead>"
                + "</hr:author>"
                + "<lib:character id=\"Snoopy\">"
                + "<hr:name>Snoopy</hr:name>"
                + "<hr:born>1950-10-04</hr:born>"
                + "<lib:qualification>extroverted beagle</lib:qualification>"
                + "</lib:character>"
                + "</lib:book>"
                + "</lib:library>"
                + "</soapenv:Body></soapenv:Envelope>";
    }

}
