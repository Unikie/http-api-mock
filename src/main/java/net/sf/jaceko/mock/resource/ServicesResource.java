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

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dto.*;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.service.MockConfigurationHolder;
import org.jboss.resteasy.spi.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Path("/services")
public class ServicesResource {

    private MockConfigurationHolder mockConfigurationService;

    @GET
    @Produces({MediaType.APPLICATION_XML})
    public WebServicesDto getWebServices(@Context HttpServletRequest context) {
        Collection<WebService> servicesCollection = mockConfigurationService.getWebServices();
        WebServicesDto webServicesDto = new WebServicesDto();
        for (WebService webService : servicesCollection) {
            WebServiceDto webServiceDto = new WebServiceDto();
            webServiceDto.setName(webService.getName());
            webServiceDto.setType(webService.getServiceType());
            String serviceUri = getServiceUri(context, webService.getServiceType(), webService.getName());
            webServiceDto.setEndpointUri(getEndpointUri(serviceUri));
            if (ServiceType.SOAP == webService.getServiceType()) {
                webServiceDto.setWsdlUri(getWsdlUri(serviceUri));
            } else if(ServiceType.REST == webService.getServiceType()) {
                webServiceDto.setMultipartEndpointUri(getMultipartEndpointUri(serviceUri));
            }
            Collection<WebserviceOperation> operations = webService.getOperations();
            for (WebserviceOperation operation : operations) {
                OperationRefDto operationRef = new OperationRefDto();
                operationRef.setName(operation.getOperationName());
                operationRef.setUri(getOperationUri(serviceUri, operation.getOperationName()));
                webServiceDto.getOperationRefs().add(operationRef);
            }

            webServicesDto.getWebservicesList().add(webServiceDto);
        }

        return webServicesDto;
    }

    @Path("/{serviceType}/{serviceName}/operations/{operationName}")
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public OperationDto getOperation(@PathParam("serviceType") String serviceType, @PathParam("serviceName") String serviceName,
                                     @PathParam("operationName") String operationName, @Context HttpServletRequest context) {
        return getOperationDto(serviceType, serviceName, operationName, context, mockConfigurationService);
    }

    static OperationDto getOperationDto(String serviceType, String serviceName, String operationName, HttpServletRequest context, MockConfigurationHolder configurationHolder) {
        ServiceType svcType = null;
        try {
            svcType = ServiceType.valueOf(serviceType);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Unknown service type: " + serviceType);
        }

        WebService service = configurationHolder.getWebService(serviceName);
        WebserviceOperation webServiceOperation = configurationHolder.getWebServiceOperation(serviceName, operationName);
        String operationUri = getOperationUri(getServiceUri(context, svcType, serviceName), operationName);

        ResourceRefDto initResourceRef = new ResourceRefDto(operationUri, "/init", HttpMethod.POST, "operation initialization");
        ResourceRefDto postResponseResourceRef = new ResourceRefDto(operationUri, "/responses", HttpMethod.POST,
            "add custom response");
        ResourceRefDto set1stResponseResourceRef = new ResourceRefDto(operationUri, "/responses/1", HttpMethod.PUT,
            "set first custom response");
        ResourceRefDto set2ndResponseResourceRef = new ResourceRefDto(operationUri, "/responses/2", HttpMethod.PUT,
            "set second custom response");

        ResourceRefDto recordedRequestsResourceRef = new ResourceRefDto(operationUri, "/recorded-requests", HttpMethod.GET,
            "recorded requests");

        List<ResourceRefDto> verificationResources = newArrayList(recordedRequestsResourceRef);

        ResourceRefDto recordedRequestHeaders = new ResourceRefDto(operationUri, "/recorded-request-headers", HttpMethod.GET,
            "recorded request headers");
        verificationResources.add(recordedRequestHeaders);

        ResourceRefDto recordedRequestParams = new ResourceRefDto(operationUri, "/recorded-request-params", HttpMethod.GET,
            "recorded request parameters");
        verificationResources.add(recordedRequestParams);

        if (svcType == ServiceType.REST) {

            if (service.isEnableResourcePaths()) {
                ResourceRefDto recordedResourcePaths = new ResourceRefDto(operationUri, "/recorded-resource-paths", HttpMethod.GET,
                    "recorded resource paths");
                verificationResources.add(recordedResourcePaths);
            } else {
                ResourceRefDto recordedResourceIds = new ResourceRefDto(operationUri, "/recorded-resource-ids", HttpMethod.GET,
                    "recorded resource ids");
                verificationResources.add(recordedResourceIds);
            }
        }

        OperationDto operationDto = new OperationDto();
        operationDto.setName(webServiceOperation.getOperationName());
        operationDto.setSetupResources(newArrayList(initResourceRef, postResponseResourceRef, set1stResponseResourceRef,
            set2ndResponseResourceRef));
        operationDto.setVerificationResources(verificationResources);

        return operationDto;
    }

    private String getWsdlUri(String serviceUri) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serviceUri);
        stringBuilder.append("/wsdl");
        return stringBuilder.toString();
    }

    private String getEndpointUri(String serviceUri) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serviceUri);
        stringBuilder.append("/endpoint");
        return stringBuilder.toString();
    }

    private String getMultipartEndpointUri(String serviceUri) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serviceUri);
        stringBuilder.append("/multipart-endpoint");
        return stringBuilder.toString();
    }

    private static String getOperationUri(String serviceUri, String operationName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serviceUri);
        stringBuilder.append("/operations/");
        stringBuilder.append(operationName);
        return stringBuilder.toString();
    }

    private static String getServiceUri(HttpServletRequest context, ServiceType serviceType, String serviceName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://");
        stringBuilder.append(context.getServerName());
        stringBuilder.append(":");
        stringBuilder.append(context.getServerPort());
        stringBuilder.append(context.getContextPath());
        stringBuilder.append("/services/");
        stringBuilder.append(serviceType);
        stringBuilder.append("/");
        stringBuilder.append(serviceName);
        return stringBuilder.toString();
    }

    public void setMockConfigurationService(MockConfigurationHolder mockConfigurationService) {
        this.mockConfigurationService = mockConfigurationService;
    }

}
