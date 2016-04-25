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
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

public class MockSetupExecutor {
    private MockConfigurationHolder configurationHolder;
    private RecordedRequestsHolder recordedRequestsHolder;


    public void setCustomResponse(String serviceName, String operationId, int requestInOrder, MockResponse response) {
        WebserviceOperation serviceOperation = configurationHolder.getWebServiceOperation(serviceName, operationId);
        serviceOperation.setCustomResponse(response, requestInOrder);
        serviceOperation.resetInvocationNumber();

    }

    public void addCustomResponse(String serviceName, String operationId, MockResponse response) {
        WebserviceOperation serviceOperation = configurationHolder.getWebServiceOperation(serviceName, operationId);
        serviceOperation.addCustomResponse(response);

    }

    public void initMock(String serviceName, String operationId) {
        recordedRequestsHolder.clearRecordedRequests(serviceName, operationId);
        WebserviceOperation serviceOperation = configurationHolder.getWebServiceOperation(serviceName, operationId);
        serviceOperation.init();
    }

    public void setMockserviceConfiguration(MockConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    public void setRecordedRequestsHolder(RecordedRequestsHolder recordedRequestsHolder) {
        this.recordedRequestsHolder = recordedRequestsHolder;
    }


}
