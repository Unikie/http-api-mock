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
package net.sf.jaceko.mock.model.webservice;

import com.google.common.base.Objects;

import net.sf.jaceko.mock.application.enums.ServiceType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebService {

    private String name;
    private boolean ignoreXmlDeclaration = true;
    private Map<Integer, WebserviceOperation> indxToOperationMap = new HashMap<Integer, WebserviceOperation>();
    private String wsdlText;

    private ServiceType serviceType;
    private boolean enableResourcePaths = false;

    public WebService() {
        super();
    }

    public WebService(String name, String wsdlText) {
        super();
        this.name = name;
        this.wsdlText = wsdlText;
    }

    public void addOperation(int operationIndex, WebserviceOperation operation) {
        indxToOperationMap.put(operationIndex, operation);
    }

    public void addOperations(List<WebserviceOperation> webserviceOperations) {
        int i = 0;
        for (WebserviceOperation webserviceOperation : webserviceOperations) {
            addOperation(i++, webserviceOperation);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof WebService) {
            WebService that = (WebService) object;
            return Objects.equal(this.name, that.name)
                && Objects.equal(this.indxToOperationMap, that.indxToOperationMap)
                && Objects.equal(this.wsdlText, that.wsdlText)
                && Objects.equal(this.serviceType, that.serviceType);
        }
        return false;
    }

    public String getName() {
        return name;
    }



    public WebserviceOperation getOperation(int indx) {
        return indxToOperationMap.get(indx);
    }

    public WebserviceOperation getOperation(String name) {
        Collection<WebserviceOperation> operations = getOperations();
        for (WebserviceOperation operation : operations) {
            if (operation.getOperationName().equals(name)) {
                return operation;
            }
        }
        return null;

    }

    public Collection<WebserviceOperation> getOperations() {
        return indxToOperationMap.values();
    }

    public ServiceType getServiceType() {
	    return this.serviceType;
	}

    public String getWsdlText() {
        return wsdlText;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, indxToOperationMap, wsdlText, serviceType);
    }

    public boolean isEnableResourcePaths() {
        return enableResourcePaths;
    }


    public boolean isIgnoreXmlDeclaration() {
        return ignoreXmlDeclaration;
    }

    public void setEnableResourcePaths(boolean enableResourcePaths) {
        this.enableResourcePaths = enableResourcePaths;
    }

    public void setIgnoreXmlDeclaration(boolean ignoreXmlDeclaration) {
        this.ignoreXmlDeclaration = ignoreXmlDeclaration;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setServiceType(ServiceType serviceType) {
	    this.serviceType = serviceType;
	}

    public void setWsdlText(String wsdlText) {
        this.wsdlText = wsdlText;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("indxToOperationMap", indxToOperationMap)
            .add("wsdlText", wsdlText)
            .add("serviceType", serviceType)
            .toString();
    }
}
