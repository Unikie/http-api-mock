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

import net.sf.jaceko.mock.model.request.MockRequest;
import net.sf.jaceko.mock.service.RecordedRequestsHolder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.text.MessageFormat.format;

public class BasicVerificationResource {

    protected RecordedRequestsHolder recordedRequestsHolder;

    public BasicVerificationResource() {
        super();
    }

    @GET
    @Path("/recorded-requests")
    @Produces(MediaType.TEXT_XML)
    public String getRecordedRequests(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @DefaultValue("") @QueryParam("requestElement") String requestElement) {

        Collection<String> recordedRequests = recordedRequestsHolder.getRecordedRequestBodies(
            serviceName, operationId);
        return buildListXml(recordedRequests, "recorded-requests", requestElement, false);
    }

    @GET
    @Path("/recorded-request-headers")
    @Produces(MediaType.TEXT_XML)
    public RecordedRequestHeaders getRecordedRequestHeaders(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {

        Collection<MockRequest> recordedRequests = recordedRequestsHolder.getRecordedRequests(serviceName, operationId);

        RecordedRequestHeaders recordedRequestHeaders = new RecordedRequestHeaders();
        for (MockRequest recordedRequest : recordedRequests) {
            recordedRequestHeaders.addRequestHeaders(recordedRequest.getHeaders());
        }

        return recordedRequestHeaders;
    }

    @GET
    @Path("/recorded-request-params")
    @Produces(MediaType.TEXT_XML)
    public String getRecordedUrlParams(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
        Collection<String> recordedUrlParams = recordedRequestsHolder.getRecordedUrlParams(
            serviceName, operationId);
        return buildRequestParamsXml(recordedUrlParams);
    }

    private String buildRequestParamsXml(Collection<String> recordedUrlParams) {
        String rootElementName = "recorded-request-params";
        String elementName = "recorded-request-param";
        boolean surroundElementTextWithCdata = true;
        return buildListXml(recordedUrlParams, rootElementName, elementName,
            surroundElementTextWithCdata);
    }

    protected String buildListXml(Collection<String> elementValuesList, String rootElementName, String elementName, boolean surroundElementTextWithCdata) {
        StringBuilder builder = new StringBuilder();
        builder.append(format("<{0}>\n", rootElementName));
        for (String urlQueryString : elementValuesList) {
            if (elementNameSpecified(elementName)) {
                builder.append(format("<{0}>", elementName));
            }
            if (surroundElementTextWithCdata) {
                builder.append("<![CDATA[");
            }
            builder.append(urlQueryString);
            if (surroundElementTextWithCdata) {
                builder.append("]]>");
            }
            if (elementNameSpecified(elementName)) {
                builder.append(format("</{0}>\n", elementName));
            }

        }
        builder.append(format("</{0}>", rootElementName));
        return builder.toString();
    }

    private boolean elementNameSpecified(String elementName) {
        return elementName != null && !elementName.isEmpty();
    }

    public void setRecordedRequestsHolder(RecordedRequestsHolder recordedRequestsHolder) {
        this.recordedRequestsHolder = recordedRequestsHolder;
    }

    @XmlRootElement(name = "recorded-request-headers")
    static class RecordedRequestHeaders {

        @XmlElement(name = "single-request-recorded-headers")
        private List<SingleRequestHeaders> recordedRequestHeader = new ArrayList<SingleRequestHeaders>();

        public List<SingleRequestHeaders> getHeaders() {
            return recordedRequestHeader;
        }

        public void addRequestHeaders(MultivaluedMap<String, String> headers) {

            SingleRequestHeaders singleRequestHeaders = new SingleRequestHeaders();
            for (String headerName : headers.keySet()) {
                RecordedHeader recordedHeader = new RecordedHeader(headerName, headers.getFirst(headerName));
                singleRequestHeaders.addRecordedHeader(recordedHeader);
            }
            recordedRequestHeader.add(singleRequestHeaders);
        }
    }

    static class SingleRequestHeaders {
        @XmlElement(name = "header")
        private List<RecordedHeader> recordedHeaders = new ArrayList<RecordedHeader>();

        List<RecordedHeader> getRecordedHeaders() {
            return recordedHeaders;
        }

        public void addRecordedHeader(RecordedHeader recordedHeader) {
            recordedHeaders.add(recordedHeader);
        }
    }

    static class RecordedHeader {
        @XmlElement
        private String name;
        @XmlElement
        private String value;

        RecordedHeader() {
        }

        RecordedHeader(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

}