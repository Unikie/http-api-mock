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


import javax.ws.rs.core.MultivaluedMap;

public class MockRequest {

    private String resourcePath;
    private String body;
    private String queryString;
    private MultivaluedMap<String, String> headers;

    public MockRequest(String body, String queryString, String resourcePath, MultivaluedMap<String, String> headers) {
        super();
        this.body = body;
        this.queryString = queryString;
        this.resourcePath = resourcePath;
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryString() {
        return queryString;
    }

    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }
}
