/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockSetupExecutor;
import org.apache.commons.httpclient.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class BasicSetupResource {

    private static final String HEADERS_DELIMITER = ",";
    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private MockSetupExecutor mockSetupExecutor;

    public BasicSetupResource() {
        super();
    }

    @POST
    @Path("/{operationId}/responses")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addResponse(@Context HttpHeaders headers, @PathParam("serviceName") String serviceName,
                                @PathParam("operationId") String operationId, @QueryParam("code") int customResponseCode,
                                @QueryParam("delay") int delaySec, @QueryParam("headers") String headersToPrime,
                                String customResponseBody) {

        Map<String, String> headersMap = parseHeadersToPrime(headersToPrime);

        mockSetupExecutor.addCustomResponse(
            serviceName,
            operationId,
            MockResponse.body(customResponseBody).code(customResponseCode).contentType(headers.getMediaType())
                .delaySec(delaySec).headers(headersMap).build());
        return Response.status(HttpStatus.SC_OK).build();
    }

    static Map<String, String> parseHeadersToPrime(String headersToPrime) {
        Map<String, String> headersMap = new HashMap<String, String>();

        if (headersToPrime != null) {
            Scanner headersScanner = new Scanner(headersToPrime).useDelimiter(HEADERS_DELIMITER);
            while (headersScanner.hasNext()) {
                String header = headersScanner.next();
                String[] headerPart = header.split(HEADER_DELIMITER);
                String headerName = headerPart[HEADER_KEY_INDEX];
                String headerValue = headerPart[HEADER_VALUE_INDEX];
                headersMap.put(headerName, headerValue);
            }
        }

        return headersMap;
    }

    @PUT
    @Path("/{operationId}/responses/{requestInOrder}")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response setResponse(@Context HttpHeaders headers, @PathParam("serviceName") String serviceName,
                                @PathParam("operationId") String operationId, @PathParam("requestInOrder") int requestInOrder,
                                @QueryParam("code") int customResponseCode, @QueryParam("delay") int delaySec, @QueryParam("headers") String headersToPrime,
                                String customResponseBody) {
        Map<String, String> headersMap = parseHeadersToPrime(headersToPrime);

        mockSetupExecutor.setCustomResponse(
            serviceName,
            operationId,
            requestInOrder,
            MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec)
                .contentType(headers.getMediaType()).headers(parseHeadersToPrime(headersToPrime)).build());
        return Response.status(HttpStatus.SC_OK).build();
    }

    @POST
    @Path("/{operationId}/init")
    public Response initMock(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
        mockSetupExecutor.initMock(serviceName, operationId);
        return Response.status(HttpStatus.SC_OK).build();
    }

    public void setMockSetupExecutor(MockSetupExecutor mockSetupExecutor) {
        this.mockSetupExecutor = mockSetupExecutor;

    }

}