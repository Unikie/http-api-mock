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
import net.sf.jaceko.mock.application.enums.HttpMethod;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource-ref")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceRefDto {

    @XmlAttribute(name = "description")
    private String desc;

    @XmlAttribute(name = "http-method")
    private HttpMethod httpMethod;

    @XmlAttribute(name = "uri")
    private String uri;

    public ResourceRefDto() {
    }

    public ResourceRefDto(String rootUri, String resourceName, HttpMethod httpMethod, String desc) {
        this(rootUri + resourceName, httpMethod, desc);
    }

    public ResourceRefDto(String uri, HttpMethod httpMethod, String desc) {
        super();
        this.desc = desc;
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(httpMethod, desc, uri);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ResourceRefDto) {
            ResourceRefDto that = (ResourceRefDto) object;
            return Objects.equal(this.httpMethod, that.httpMethod) && Objects.equal(this.desc, that.desc)
                && Objects.equal(this.uri, that.uri);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("httpMethod", httpMethod).add("desc", desc).add("uri", uri).toString();
    }

}
