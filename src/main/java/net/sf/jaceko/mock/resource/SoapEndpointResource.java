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
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import net.sf.jaceko.mock.service.RequestExecutor;
import net.sf.jaceko.mock.util.SOAPMessageParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/services/SOAP/{serviceName}/endpoint")
public class SoapEndpointResource {
    private static final Logger LOG = Logger.getLogger(SoapEndpointResource.class);

    private RequestExecutor service;
    private MockConfigurationHolder configurationHolder;

    @POST
    @Consumes({MediaType.TEXT_XML, "application/soap+xml", "application/xml"})
    @Produces(MediaType.TEXT_XML)
    public Response performRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest httpServletRequest,
                                   String request, @Context HttpHeaders headers) {
        LOG.debug("serviceName: " + serviceName + ", request:" + request);
        String requestMessageName = SOAPMessageParser.extractRequestMessageName(request);

        String responseBody = null;
        int code = 200;
        MockResponse response = service.performRequest(serviceName, requestMessageName,
            request, httpServletRequest.getQueryString(), null, headers.getRequestHeaders());
        if (response != null) {
            return buildWebserviceResponse(response);
        }
        LOG.debug("serviceName: " + serviceName + ", response:" + responseBody + " ,code: " + code);
        return null;
    }

    private Response buildWebserviceResponse(MockResponse mockResponse) {
        Object responseEntity;

        if(mockResponse.isBinary()) {
            responseEntity = mockResponse.getBinaryBody();
        } else {
            responseEntity = mockResponse.getBody();
        }
        Response.ResponseBuilder responseBuilder = Response.status(mockResponse.getCode()).entity(responseEntity).type(mockResponse.getContentType());

        addHeadersToResponse(mockResponse.getHeaders(), responseBuilder);

        return responseBuilder.build();
    }

    private void addHeadersToResponse(Map<String, String> headersToReturn, Response.ResponseBuilder responseBuilder) {
        if (headersToReturn != null) {

            for (String headerKey : headersToReturn.keySet()) {
                String headerValue = headersToReturn.get(headerKey);
                responseBuilder.header(headerKey, headerValue);
            }
        }
    }




    public void setConfigurationHolder(MockConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    public void setWebserviceMockService(RequestExecutor service) {
        this.service = service;
    }

}
