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

import net.sf.jaceko.mock.exception.ClientFaultException;
import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.util.SOAPMessageParser;
import org.apache.commons.collections.list.GrowthList;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;

/**
 * Class representing an operation or "method" of a webservice. SOAP operations
 * are identified by a root node name of an xml request eg. prepayRequest. REST
 * operations are identified by a HTTP method name eg. GET, POST, PUT, DELETE
 */
public class WebserviceOperation {

    private static final Logger LOG = Logger.getLogger(WebserviceOperation.class);

    private String operationName;
    private String defaultResponseFile;
    private String defaultResponseText;
    private byte[] defaultResponseBinaryContent;
    private int defaultResponseCode = 200;
    private String defaultResponseContentType = MediaType.TEXT_XML_TYPE.toString();
    private Map<String, String> defaultHeaders = new HashMap<String, String>();
    private final AtomicInteger invocationNumber = new AtomicInteger(0);
    private boolean binary = false;
    private String[] nameSpaces;
    private String request;

    private static final String MISSING_NAMESPACE = "Message doesn't contain namespace";
    private static final String INVALID_NAMESPACE = "The namespace of message doesn't match ";


    @SuppressWarnings("unchecked")
    private final List<MockResponse> customResponses = synchronizedList(new GrowthList());


    public WebserviceOperation() {
        super();
    }

    public static WebserviceOperationBuilder name(String operationName) {
        WebserviceOperationBuilder builder = new WebserviceOperationBuilder();
        return builder.operationName(operationName);
    }

    public static WebserviceOperationBuilder defaultResponseText(String defaultResponseText) {
        WebserviceOperationBuilder builder = new WebserviceOperationBuilder();
        return builder.defaultResponseText(defaultResponseText);
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getDefaultResponseFile() {
        return defaultResponseFile;
    }

    public void setDefaultResponseFile(String defaultResponseFile) {
        this.defaultResponseFile = defaultResponseFile;
    }

    public String getDefaultResponseText() {
        return this.defaultResponseText;
    }

    public void setDefaultResponseText(String defaultResponseText) {
        this.defaultResponseText = defaultResponseText;
    }

    public MockResponse getResponse(int requestNumber) {
        int index = requestNumber - 1;

        validateNameSpace();

        MockResponse mockResponse = null;
        synchronized (customResponses) {
            if (customResponses.size() < requestNumber || (mockResponse = customResponses.get(index)) == null) {
                MockResponse response = MockResponse.body(defaultResponseText)
                    .binaryBody(defaultResponseBinaryContent)
                    .code(defaultResponseCode)
                    .contentType(defaultResponseContentType)
                    .build();

                if(!getDefaultResponseHeaders().isEmpty()) {
                    response.setHeaders(defaultHeaders);
                }

                if( isBinary() ) {
                    // browser uses this as file name for download
                    response.addHeader("Content-Disposition", "attachment; filename=" + defaultResponseFile);
                }
                return response;
            }
            return mockResponse;
        }
    }

    public void setCustomResponse(MockResponse customResponse, int requestNumber) {
        customResponse.setZeroCodeTo(defaultResponseCode);
        customResponses.set(requestNumber - 1, customResponse);
    }

    public void addCustomResponse(MockResponse customResponse) {
        customResponse.setZeroCodeTo(defaultResponseCode);
        customResponses.add(customResponse);

    }

    public void init() {
        customResponses.clear();
        resetInvocationNumber();
    }

    /**
     * increments and returns number of consecutive service invocations
     */
    public int getNextInvocationNumber() {
        return invocationNumber.incrementAndGet();
    }

    public void resetInvocationNumber() {
        invocationNumber.set(0);

    }

    public int getDefaultResponseCode() {
        return defaultResponseCode;
    }

    public void setDefaultResponseCode(int defaultResponseCode) {
        this.defaultResponseCode = defaultResponseCode;
    }

    public String getDefaultResponseContentType() {
        return defaultResponseContentType;
    }

    public void setDefaultResponseContentType(String defaultResponseContentType) {
        this.defaultResponseContentType = defaultResponseContentType;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public void setDefaultResponseBinaryContent(byte[] defaultResponseBinaryContent) {
        this.defaultResponseBinaryContent = defaultResponseBinaryContent;
    }

    public void setNameSpaces(String nameSpaces) {
        if(nameSpaces.isEmpty()) return;
        if (nameSpaces.indexOf(",") > 0) {
            this.nameSpaces = nameSpaces.split(",");
            Arrays.sort(this.nameSpaces);
        } else {
            this.nameSpaces = new String[1];
            this.nameSpaces[0] = nameSpaces;
        }
    }

    public String[] getNameSpaces() {
        return nameSpaces;
    }

    public byte[] getDefaultResponseBinaryContent() {
        return defaultResponseBinaryContent;
    }

    private void validateNameSpace() {
        Node rootElement;
        try {
            rootElement = SOAPMessageParser.getRequestMessageNode(request);
        } catch (ClientFaultException e) {
            LOG.debug("Message was not SOAP message, won't validate.");
            return;
        }

        if (getNameSpaces() == null || getNameSpaces().length == 0) {
            LOG.debug("Nothing to validate...");
            return;
        }
        if (rootElement.getNamespaceURI() == null || rootElement.getNamespaceURI().isEmpty()) {
            throw new ClientFaultException(MISSING_NAMESPACE);
        }

        boolean match = false;
        for (String namespace : getNameSpaces()) {
            if(rootElement.getNamespaceURI().equals(namespace)) match = true;
        }

        if(!match)
            throw new ClientFaultException(INVALID_NAMESPACE);
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return format(
            "WebserviceOperation [operationName=%s, defaultResponseFile=%s, binary=%s, defaultResponseText=%s, defaultResponseCode=%s, defaultResponseContentType=%s, invocationNumber=%s, customResponses=%s, defaultHeaders=%s]",
            operationName, defaultResponseFile, binary, defaultResponseDebugText(), defaultResponseCode, defaultResponseContentType,
            invocationNumber, customResponses != null ? customResponses.subList(0, Math.min(customResponses.size(), maxLen))
                : null, getDefaultResponseHeaders());
    }

    private String defaultResponseDebugText() {
        return isBinary() ? "<binary content>" : defaultResponseText;
    }

    public void setDefaultResponseHeaders(String propertyValue) {
        if(propertyValue == null || propertyValue.trim().equals("")) return;
        defaultHeaders.clear();
        for (String header : propertyValue.split(",")) {
            String[] parts = header.split((":"));
            defaultHeaders.put(parts[0], parts[1]);
        }
    }

    public String getDefaultResponseHeaders() {
        if(defaultHeaders.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        Set<String> keys = defaultHeaders.keySet();
        for(String key : keys) {
            sb.append(key);
            sb.append(":");
            sb.append(defaultHeaders.get(key));
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public static class WebserviceOperationBuilder {
        private String operationName;
        private String defaultResponseFile;
        private String defaultResponseText;
        private byte[] defaultResponseBinaryContent;
        private int defaultResponseCode;
        private String defaultResponseContentType;
        private String defaultResponseHeaders;
        private boolean binary;

        public WebserviceOperationBuilder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseFile(String defaultResponseFile) {
            this.defaultResponseFile = defaultResponseFile;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseText(String defaultResponseText) {
            this.defaultResponseText = defaultResponseText;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseCode(int defaultResponseCode) {
            this.defaultResponseCode = defaultResponseCode;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseContentType(String defaultResponseContentType) {
            this.defaultResponseContentType = defaultResponseContentType;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseBinaryContent(byte[] defaultResponseBinaryContent) {
            this.defaultResponseBinaryContent = defaultResponseBinaryContent;
            return this;
        }

        public WebserviceOperationBuilder binary(boolean binary) {
            this.binary = binary;
            return this;
        }

        public WebserviceOperationBuilder defaultResponseHeaders(String defaultHeaders) {
            this.defaultResponseHeaders = defaultHeaders;
            return this;
        }

        public WebserviceOperation build() {
            WebserviceOperation webserviceOperation = new WebserviceOperation();
            webserviceOperation.operationName = this.operationName;
            webserviceOperation.defaultResponseFile = this.defaultResponseFile;
            webserviceOperation.defaultResponseText = this.defaultResponseText;
            webserviceOperation.defaultResponseCode = this.defaultResponseCode;
            if (this.defaultResponseContentType != null) {
                webserviceOperation.defaultResponseContentType = this.defaultResponseContentType;
            }
            webserviceOperation.setBinary(binary);
            webserviceOperation.setDefaultResponseBinaryContent(defaultResponseBinaryContent);
            webserviceOperation.setDefaultResponseHeaders(defaultResponseHeaders);
            return webserviceOperation;
        }

    }

}
