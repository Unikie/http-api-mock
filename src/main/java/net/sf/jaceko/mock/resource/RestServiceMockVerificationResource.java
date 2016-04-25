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

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import org.jboss.resteasy.spi.NotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/services/REST/{serviceName}/operations/{operationId}")
public class RestServiceMockVerificationResource extends BasicVerificationResource {

    private MockConfigurationHolder configurationHolder;

    @GET
    @Path("/recorded-resource-ids")
    @Produces(MediaType.TEXT_XML)
    public String getRecordedResourceIds(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
        WebService service = configurationHolder.getWebService(serviceName);
        if (service == null || service.isEnableResourcePaths()) {
            throw new NotFoundException("recorder-resource-ids not available for " + serviceName);
        }
        Collection<String> recordedResourceIds = recordedRequestsHolder
            .getRecordedResourcePaths(serviceName, operationId);
        String rootElementName = "recorded-resource-ids";
        String elementName = "recorded-resource-id";
        boolean surroundElementTextWithCdata = false;
        return buildListXml(recordedResourceIds, rootElementName, elementName,
            surroundElementTextWithCdata);
    }

    @GET
    @Path("/recorded-resource-paths")
    @Produces(MediaType.TEXT_XML)
    public String getRecordedResourcePaths(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
        WebService service = configurationHolder.getWebService(serviceName);
        if (service == null || !service.isEnableResourcePaths()) {
            throw new NotFoundException("recorder-resource-paths not available for " + serviceName);
        }
        Collection<String> recordedResourcePaths = recordedRequestsHolder
            .getRecordedResourcePaths(serviceName, operationId);
        String rootElementName = "recorded-resource-paths";
        String elementName = "recorded-resource-path";
        boolean surroundElementTextWithCdata = false;
        return buildListXml(recordedResourcePaths, rootElementName, elementName,
            surroundElementTextWithCdata);
    }


    public void setConfigurationHolder(MockConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }
}
