/**
 *
 *     Copyright (C) 2016 Mystes Oy
 *
 *     This file is part of HTTP API Mock.
 *
 *     HTTP API Mock is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     HTTP API Mock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import net.sf.jaceko.mock.service.RequestExecutor;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * Created by esa on 21/09/15.
 */

@Path("/services/REST/{serviceName}/multipart-endpoint")
public class RestMultipartEndpointResource {
    private static final Logger LOG = Logger.getLogger(RestEndpointResource.class);
    private static final MediaType[] TEXT_MEDIA_TYPES = {MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE, MediaType.TEXT_XML_TYPE};

    private RequestExecutor svcLayer;
    private MockConfigurationHolder configurationHolder;


    @PUT
    @Consumes({"multipart/*"})
    public Response performPutRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest httpServletRequest, @Context HttpHeaders headers, MultipartInput request) {
        return performPutRequest(serviceName, httpServletRequest, null, headers, request);
    }

    @PUT
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    @Consumes({"multipart/*"})
    public Response performPutRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest httpServletRequest, @PathParam("postfix") String resourcePath,
                                      @Context HttpHeaders headers, MultipartInput request) {
        validateResourcePath(serviceName, resourcePath);
        MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.PUT.toString(), identifyNonBinaryPart(request), null, resourcePath, headers.getRequestHeaders());
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);
    }

    @POST
    @Consumes({"multipart/*"})
    public Response performPostRequest(@PathParam("serviceName") String serviceName,
                                       @Context HttpServletRequest httpServletRequest, @Context HttpHeaders headers, MultipartInput request) {
        return performPostRequest(serviceName, httpServletRequest, null, headers, request);
    }


    @POST
    @Path("/{postfix: (([^/]+?(/)?)+?)}")
    @Consumes({"multipart/*"})
    public Response performPostRequest(@PathParam("serviceName") String serviceName,
                                       @Context HttpServletRequest httpServletRequest, @PathParam("postfix") String resourcePath, @Context HttpHeaders headers, MultipartInput request) {
        validateResourcePath(serviceName, resourcePath);

        MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), identifyNonBinaryPart(request),
                httpServletRequest.getQueryString(), resourcePath, headers.getRequestHeaders());
        LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
        return buildWebserviceResponse(mockResponse);
    }


    public void setWebserviceMockService(RequestExecutor svcLayer) {
        this.svcLayer = svcLayer;
    }

    public void setMockConfigurationHolder(MockConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    private String identifyNonBinaryPart(MultipartInput request) {
        final List<InputPart> parts = request.getParts();

        for (InputPart part : parts) {
            if (isNonBinaryPart(part)) {
                try {
                    return part.getBodyAsString();
                } catch (IOException e) {
                    throw new RuntimeException("Error while parsing text content from a multipart request", e);
                }
            }
        }


        throw new RuntimeException("non-binary part not found from request");
    }

    private boolean isNonBinaryPart(InputPart part) {
        for (MediaType mediaType : TEXT_MEDIA_TYPES) {
            if (mediaType.isCompatible(part.getMediaType())) {
                return true;
            }
        }
        return false;
    }

    private Response buildWebserviceResponse(MockResponse mockResponse) {
        return RestEndpointResourceUtil.buildWebserviceResponse(mockResponse);
    }

    private void validateResourcePath(String serviceName, String resourcePath) {
        RestEndpointResourceUtil.validateResourcePath(configurationHolder.getWebService(serviceName), resourcePath);
    }
}
