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

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.model.webservice.WebService;
import org.jboss.resteasy.spi.NotFoundException;

import javax.ws.rs.core.Response;
import java.util.Map;

final class RestEndpointResourceUtil {

    static Response buildWebserviceResponse(MockResponse mockResponse) {
        Object responseEntity;

        if( mockResponse.isBinary() ) {
            responseEntity = mockResponse.getBinaryBody();
        } else {
            responseEntity = mockResponse.getBody();
        }
        Response.ResponseBuilder responseBuilder = Response.status(mockResponse.getCode()).entity(responseEntity).type(mockResponse.getContentType());

        addHeadersToResponse(mockResponse.getHeaders(), responseBuilder);

        return responseBuilder.build();
    }

    static void validateResourcePath(WebService webService, String resourcePath) {
        if (resourcePath != null && resourcePath.indexOf("/") > 0) {

            if (!webService.isEnableResourcePaths()) {
                throw new NotFoundException("Resource paths not enabled for " + webService.getName() + " but request contained path " + resourcePath);
            }
        }
    }

    private static void addHeadersToResponse(Map<String, String> headersToReturn, Response.ResponseBuilder responseBuilder) {
        if (headersToReturn != null) {

            for (String headerKey : headersToReturn.keySet()) {
                String headerValue = headersToReturn.get(headerKey);
                responseBuilder.header(headerKey, headerValue);
            }
        }
    }
}
