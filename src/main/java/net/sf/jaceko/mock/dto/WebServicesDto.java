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
package net.sf.jaceko.mock.dto;

import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "services")
public class WebServicesDto {

    private final List<WebServiceDto> webservicesList = new ArrayList<WebServiceDto>();

    @XmlElementWrapper(name = "services")
    @XmlElement(name = "service")
    public List<WebServiceDto> getWebservicesList() {
        return webservicesList;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(webservicesList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WebServicesDto) {
            final WebServicesDto other = (WebServicesDto) obj;
            return Objects.equal(webservicesList, other.webservicesList);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(webservicesList).toString();
    }

}
