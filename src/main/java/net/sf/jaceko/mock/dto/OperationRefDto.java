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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "operation-ref")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationRefDto {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "uri")
    private String uri;

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("uri", uri)
            .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, uri);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof OperationRefDto) {
            OperationRefDto that = (OperationRefDto) object;
            return Objects.equal(this.name, that.name)
                && Objects.equal(this.uri, that.uri);
        }
        return false;
    }


}
