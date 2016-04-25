/**
 * Copyright (C) 2012 Jacek Obarymski
 * <p/>
 * This file is part of SOAP/REST Mock Service.
 * <p/>
 * SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License, version 3
 * as published by the Free Software Foundation.
 * <p/>
 * SOAP/REST Mock Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with SOAP/REST Mock Service; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dto.OperationDto;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import net.sf.jaceko.mock.service.RequestExecutor;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Path("/services/REST/{serviceName}/endpoint")
public class RestEndpointResource {
    private static final Logger LOG = Logger.getLogger(RestEndpointResource.class);

    private RequestExecutor svcLayer;
    private MockConfigurationHolder configurationHolder;

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request, @Context HttpHeaders headers) {
        return performGetRequest(serviceName, request, null, headers);
    }

    @GET
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request,
                                      @PathParam("postfix") String resourcePath, @Context HttpHeaders headers) {
        MockResponse mockResponse;

        // special case for GET: check if the mock resources of one of the operations of this service
        String operationsPathPrefix = "operations/";
        if (resourcePath != null && resourcePath.startsWith(operationsPathPrefix)) {
            String operationName = resourcePath.substring(operationsPathPrefix.length());
            OperationDto operationDto = ServicesResource.getOperationDto(ServiceType.REST.toString(), serviceName,
                    operationName, request, configurationHolder);
            String operationsXml = marshalOperationDto(operationDto);
            mockResponse = MockResponse.body(operationsXml).contentType(MediaType.APPLICATION_XML_TYPE).build();
        } else {
            // if resourcePath is actual path (not just resource id), we refuse the request if resource
            // paths are not enabled for service (keep backward compatibility)
            validateResourcePath(serviceName, resourcePath);

            // otherwise process mock request
            mockResponse = svcLayer.performRequest(serviceName, HttpMethod.GET.toString(), "", request.getQueryString(),
                    resourcePath, headers.getRequestHeaders());
        }
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);
    }

    @POST
    @Consumes({"text/*", "application/*"})
    public Response performPostRequest(@PathParam("serviceName") String serviceName,
                                       @Context HttpServletRequest httpServletRequest, @Context HttpHeaders headers, String request) {
        return performPostRequest(serviceName, httpServletRequest, null, headers, request);
    }

    @POST
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    @Consumes({"text/*", "application/*", "multipart/*"})
    public Response performPostRequest(@PathParam("serviceName") String serviceName,
                                       @Context HttpServletRequest httpServletRequest, @PathParam("postfix") String resourcePath, @Context HttpHeaders headers, String request) {
        validateResourcePath(serviceName, resourcePath);

        MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), request,
                httpServletRequest.getQueryString(), resourcePath, headers.getRequestHeaders());
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);
    }

    @PUT
    @Consumes({"text/*", "application/*"})
    public Response performPutRequest(@PathParam("serviceName") String serviceName, @Context HttpHeaders headers, String request) {
        return performPutRequest(serviceName, null, headers, request);
    }

    @PUT
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    @Consumes({"text/*", "application/*"})
    public Response performPutRequest(@PathParam("serviceName") String serviceName, @PathParam("postfix") String resourcePath,
                                      @Context HttpHeaders headers, String request) {
        validateResourcePath(serviceName, resourcePath);
        MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.PUT.toString(), request, null, resourcePath, headers.getRequestHeaders());
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);
    }

    @DELETE
    public Response performDeleteRequest(@PathParam("serviceName") String serviceName, @Context HttpHeaders headers) {
        return performDeleteRequest(serviceName, null, headers);
    }

    @DELETE
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    public Response performDeleteRequest(@PathParam("serviceName") String serviceName, @PathParam("postfix") String resourcePath, @Context HttpHeaders headers) {
        validateResourcePath(serviceName, resourcePath);
        MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.DELETE.toString(), "", null, resourcePath, headers.getRequestHeaders());
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);

    }

    private Response buildWebserviceResponse(MockResponse mockResponse) {
        return RestEndpointResourceUtil.buildWebserviceResponse(mockResponse);
    }

    private void validateResourcePath(String serviceName, String resourcePath) {
        RestEndpointResourceUtil.validateResourcePath(configurationHolder.getWebService(serviceName), resourcePath);
    }

    private static final String marshalOperationDto(OperationDto operationDto) {
        final StringWriter w = new StringWriter();
        try {
            Marshaller m = JAXBContext.newInstance(OperationDto.class)
                    .createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(operationDto, w);
        } catch (JAXBException e) {
            throw new RuntimeException("Error while marshaling operationDto ", e);
        }

        return w.toString();
    }

    public void setWebserviceMockService(RequestExecutor svcLayer) {
        this.svcLayer = svcLayer;
    }

    public void setMockConfigurationHolder(MockConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }
}
