/**
 * Copyright (C) 2012 Jacek Obarymski
 * <p/>
 * This file is part of SOAP/REST Mock Service.
 * <p/>
 * SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License, version 3
 * as published by the Free Software Foundation.
 * <p/>
 * SOAP/REST Mock Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with SOAP/REST Mock Service; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.it.helper.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpRequestSender {

    private HttpClient httpclient = new DefaultHttpClient();


    public MockResponse sendPostRequest(String url, String requestBody, String mediaType) throws
            IOException {
        return sendPostRequest(url, requestBody, mediaType, new HashMap<String, String>());
    }

    public MockResponse sendPostRequest(String url, String requestBody, String mediaType, Map<String, String> headers) throws
            IOException {
        return sendPostRequest(url,requestBody,mediaType, headers, null);
    }
    
    public MockResponse sendPostRequest(String url, String requestBody, String mediaType, Map<String,String> headers, List<RestAttachment> attachments) throws IOException {
    	HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
        addRequestBody(httpRequest, requestBody, mediaType, attachments);
        return executeRequest(httpRequest, headers);
    }

	public MockResponse sendPutRequest(String url, String requestBody, String mediaType) throws
            IOException {
        return sendPutRequest(url, requestBody, mediaType, new HashMap<String, String>());
    }

    public MockResponse sendPutRequest(String url, String requestBody, String mediaType, Map<String, String> headers) throws
            IOException {
        return sendPutRequest(url, requestBody,mediaType,headers,null);
    }

    public MockResponse sendPutRequest(String url, String requestBody, String mediaType, Map<String,String> headers, List<RestAttachment> attachments) throws IOException {
        HttpEntityEnclosingRequestBase httpRequest = new HttpPut(url);
        addRequestBody(httpRequest, requestBody, mediaType, attachments);
        return executeRequest(httpRequest, headers);
    }

    public MockResponse sendGetRequest(String url, Map<String, String> headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet, headers);
    }

    public MockResponse sendGetRequest(String url) throws IOException {
        return sendGetRequest(url, false);
    }

    public MockResponse sendGetRequest(String url, boolean binary) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet, binary, new HashMap<String, String>());
    }

    public MockResponse sendDeleteRequest(String url) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        return executeRequest(httpDelete, new HashMap<String, String>());
    }

    public MockResponse sendDeleteRequest(String url, Map<String, String> headers) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        return executeRequest(httpDelete, headers);
    }

    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }
    
    public static final class RestAttachment {
    	private final String mediaType;
    	private final byte[] binaryContent;
    	
		public RestAttachment(String mediaType, byte[] binaryContent) {
			this.mediaType = mediaType;
			this.binaryContent = binaryContent;
		}

		public String getMediaType() {
			return mediaType;
		}

		public byte[] getBinaryContent() {
			return binaryContent;
		}		
    }
    
    private MockResponse executeRequest(HttpRequestBase httpRequest, boolean binary) throws IOException {
        HttpResponse response = httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        ContentType contentType = ContentType.getOrDefault(entity);

        String body = null;
        byte[] binaryBody = null;

        if (entity != null) {
            if( binary ){
                binaryBody = EntityUtils.toByteArray(entity);
            } else {
                body = EntityUtils.toString(entity);
            }
            entity.getContent().close();
        }
        Map<String, String> headers = new HashMap<String, String>();
        Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }
        int responseCode = response.getStatusLine().getStatusCode();
        return MockResponse.body(body).binaryBody(binaryBody).code(responseCode).contentType(MediaType.valueOf(contentType.getMimeType())).headers(headers).build();
    }
    
    private MockResponse executeRequest(HttpRequestBase httpRequest, Map<String, String> headers) throws IOException {
        return executeRequest(httpRequest, false, headers);
    }

    private MockResponse executeRequest(HttpRequestBase httpRequest, boolean binary, Map<String, String> headers) throws IOException {
        for (String headerName : headers.keySet()) {
            httpRequest.addHeader(headerName, headers.get(headerName));
        }
        return executeRequest(httpRequest, binary);
    }
    
    private void addRequestBody(HttpEntityEnclosingRequestBase httpRequest, String requestBody, String mediaType, List<RestAttachment> attachments)
            throws UnsupportedEncodingException {
        boolean multipart = attachments != null && attachments.size() > 0;

        if (multipart) {
            constructMultipartRequest(httpRequest, requestBody, mediaType, attachments);
        } else {
            constructPlainRequest(httpRequest, requestBody, mediaType);
        }
    }

    private void constructPlainRequest(HttpEntityEnclosingRequestBase httpRequest, String requestBody, String mediaType) throws UnsupportedEncodingException {
        StringBuilder contentType = new StringBuilder();
        contentType.append(mediaType);
        contentType.append(";charset=UTF-8");
        httpRequest.setHeader("Content-Type", contentType.toString());

        if (requestBody != null) {
            HttpEntity requestEntity = new StringEntity(requestBody);
            httpRequest.setEntity(requestEntity);
        }
    }

    private void constructMultipartRequest(HttpEntityEnclosingRequestBase httpRequest, String requestBody, String mediaType, List<RestAttachment> attachments) throws UnsupportedEncodingException {
        MultipartEntity multipartEntity = new MultipartEntity();
        if (requestBody != null) {
            multipartEntity.addPart("payload", new StringBody(requestBody, mediaType, Charset.forName("UTF-8") ));
        }
        int attachmentCount=0;
        for (RestAttachment attachment : attachments) {
            String attachmentName = "attachment" + (++attachmentCount);
            multipartEntity.addPart(attachmentName, new ByteArrayBody(attachment.getBinaryContent(), attachment.getMediaType(), attachmentName));
        }

        httpRequest.setEntity(multipartEntity);
    }
}
