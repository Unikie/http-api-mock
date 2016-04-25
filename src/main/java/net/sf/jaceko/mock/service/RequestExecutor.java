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
package net.sf.jaceko.mock.service;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import javax.ws.rs.core.MultivaluedMap;

public class RequestExecutor {
    private MockConfigurationHolder configurationHolder;

    private Delayer delayService;

    private RecordedRequestsHolder recordedRequestsHolder;

    public MockResponse performRequest(String serviceName, String operationId, String request, String queryString,
                                       String resourcePath, MultivaluedMap<String, String> headers) {
        WebserviceOperation serviceOperation = configurationHolder.getWebServiceOperation(serviceName, operationId);
        serviceOperation.setRequest(request);
        int invocationNumber = serviceOperation.getNextInvocationNumber();
        MockResponse response = serviceOperation.getResponse(invocationNumber);
        recordedRequestsHolder.recordRequest(serviceName, operationId, request, queryString, resourcePath, headers);
        delayService.delaySec(response.getDelaySec());
        return response;

    }

    public String getWsdl(String serviceName) {
        WebService soapService = configurationHolder.getWebService(serviceName);
        return soapService.getWsdlText();
    }

    public void setMockserviceConfiguration(MockConfigurationHolder configuration) {
        this.configurationHolder = configuration;
    }

    public void setDelayer(Delayer delayService) {
        this.delayService = delayService;
    }

    public void setRecordedRequestsHolder(RecordedRequestsHolder recordedRequestsHolder) {
        this.recordedRequestsHolder = recordedRequestsHolder;
    }

}
