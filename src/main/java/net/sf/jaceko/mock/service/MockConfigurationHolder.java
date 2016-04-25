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

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import org.jboss.resteasy.spi.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MockConfigurationHolder {

    private Map<String, WebService> servicesMap = new HashMap<String, WebService>();

    public void setWebServices(Collection<WebService> services) {
        servicesMap = new HashMap<String, WebService>();
        for (WebService soapService : services) {
            servicesMap.put(soapService.getName(), soapService);
        }
    }

    public Collection<WebService> getWebServices() {
        return servicesMap.values();
    }

    public WebserviceOperation getWebServiceOperation(String serviceName, String operationId) {
        WebService service = getWebService(serviceName);
        WebserviceOperation operation = service.getOperation(operationId);

        if (operation == null) {
            throw new NotFoundException("Undefined webservice operation: operationId:" + operationId + " of service: "
                + serviceName);
        }
        return operation;

    }

    public WebService getWebService(String serviceName) {
        WebService service = servicesMap.get(serviceName);
        if (service == null) {
            throw new NotFoundException("Undefined webservice:" + serviceName);
        }
        return service;

    }

}
