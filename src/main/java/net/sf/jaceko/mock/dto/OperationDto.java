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

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationDto {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElementWrapper(name = "setup-resources")
    @XmlElement(name = "resource-ref")
    private List<ResourceRefDto> setupResources;

    @XmlElementWrapper(name = "verification-resources")
    @XmlElement(name = "resource-ref")
    private List<ResourceRefDto> verificationResources;

    public void setName(String name) {
        this.name = name;
    }

    public void setSetupResources(List<ResourceRefDto> setupResources) {
        this.setupResources = setupResources;

    }

    public void setVerificationResources(List<ResourceRefDto> verificationResources) {
        this.verificationResources = verificationResources;

    }

    public List<ResourceRefDto> getSetupResources() {
        return setupResources;
    }

    public List<ResourceRefDto> getVerificationResources() {
        return verificationResources;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("verificationResources", verificationResources)
            .add("setupResources", setupResources)
            .add("name", name)
            .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(verificationResources, setupResources, name);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof OperationDto) {
            OperationDto that = (OperationDto) object;
            return Objects.equal(this.verificationResources, that.verificationResources)
                && Objects.equal(this.setupResources, that.setupResources)
                && Objects.equal(this.name, that.name);
        }
        return false;
    }


}
