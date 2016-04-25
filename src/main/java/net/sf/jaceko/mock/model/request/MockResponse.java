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
package net.sf.jaceko.mock.model.request;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MockResponse {
    private String bodyString;
    private byte[] binaryBody;
    private int code = 200;
    private int delaySec;
    private String contentType;
    private Map<String, String> headers;

    public static MockResponseBuilder body(String body) {
        MockResponseBuilder builder = MockResponseBuilder.getInstance();
        builder.body(body);
        return builder.body(body);
    }

    public MockResponse(String body, int code, int delaySec) {
        super();
        this.bodyString = body;
        this.code = code;
        this.delaySec = delaySec;
    }

    public MockResponse(int delaySec) {
        super();
        this.delaySec = delaySec;
    }

    public MockResponse(String body) {
        super();
        this.bodyString = body;
    }

    public MockResponse(String body, int code) {
        this.bodyString = body;
        this.code = code;
    }

    public MockResponse() {
    }

    public String getBody() {
        return bodyString;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setZeroCodeTo(int code) {
        if (this.code == 0) {
            setCode(code);
        }
    }

    public int getDelaySec() {
        return delaySec;
    }

    public String getContentType() {
        return contentType;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String headerName, String value) {
        headers.put(headerName, value);
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setBinaryBody(byte[] body) {
        this.binaryBody = body;
    }

    public byte[] getBinaryBody() {
        return binaryBody;
    }

    public boolean isBinary() {
        return binaryBody != null;
    }

    public static class MockResponseBuilder {
        private String body;
        private int code = 200;
        private int delaySec;
        private String contentType;
        private Map<String, String> headers = new HashMap<String, String>();
        private byte[] binaryBody;

        public static MockResponseBuilder getInstance() {
            return new MockResponseBuilder();
        }

        public MockResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public MockResponseBuilder code(int code) {
            this.code = code;
            return this;
        }

        public MockResponseBuilder delaySec(int delaySec) {
            this.delaySec = delaySec;
            return this;
        }

        public MockResponseBuilder contentType(MediaType contentType) {
            if (contentType != null) {
                this.contentType = contentType.toString();
            }
            return this;
        }

        public MockResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MockResponseBuilder binaryBody(byte[] binaryBody) {
            this.binaryBody = binaryBody;
            return this;
        }

        public MockResponse build() {
            MockResponse mockResponse = new MockResponse();
            mockResponse.setBody(body);
            mockResponse.setCode(code);
            mockResponse.setContentType(contentType);
            mockResponse.setDelaySec(delaySec);
            mockResponse.setHeaders(headers);
            mockResponse.setBinaryBody(binaryBody);
            return mockResponse;
        }

        public MockResponseBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
    }

    void setBody(String body) {
        this.bodyString = body;
    }

    void setDelaySec(int delaySec) {
        this.delaySec = delaySec;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MockResponse that = (MockResponse) o;

        if (code != that.code) return false;
        if (delaySec != that.delaySec) return false;
        if (bodyString != null ? !bodyString.equals(that.bodyString) : that.bodyString != null) return false;
        if (binaryBody != null ? !Arrays.equals(binaryBody,that.binaryBody) : that.binaryBody != null) return false;
        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bodyString != null ? bodyString.hashCode() : 0;
        result = 31 * result + code;
        result = 31 * result + delaySec;
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (binaryBody != null ? binaryBody.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MockResponse{" +
            "body='" + debugBodyText() + '\'' +
            ", code=" + code +
            ", delaySec=" + delaySec +
            ", contentType='" + contentType + '\'' +
            ", headers=" + headers +
            '}';
    }

    String debugBodyText() {
        return isBinary() ? "<binary content>" : bodyString;
    }
}
