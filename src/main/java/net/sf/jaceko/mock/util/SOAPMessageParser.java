/**
 *
 *     Copyright (C) 2016 Mystes Oy
 *
 *     This file is part of HTTP API Mock.
 *
 *     HTTP API Mock is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     HTTP API Mock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package net.sf.jaceko.mock.util;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.exception.ClientFaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by jussi on 30/12/15.
 */
public class SOAPMessageParser {

    private static final String BODY = "Body";
    private static final String ENVELOPE = "Envelope";
    private static final String INVALID_SOAP_REQUEST = "Invalid SOAP request";
    private static final String MALFORMED_XML = "Malformed Xml";


    public static String extractRequestMessageName(String request) {
        Node requestMessage = getRequestMessageNode(request);

        if (requestMessage == null) {
            throw new ClientFaultException(INVALID_SOAP_REQUEST);
        }
        return requestMessage.getLocalName();

    }

    public static Node getRequestMessageNode(String request) {
        Document reqDocument = null;

        try {
            reqDocument = new DocumentImpl(request, true);
        } catch (Exception e) {
            throw new ClientFaultException(MALFORMED_XML, e);
        }

        reqDocument.normalize();
        Node envelope = getChildElement(reqDocument, ENVELOPE);
        Node body = getChildElement(envelope, BODY);
        Node requestMessage = getChildElement(body);

        return requestMessage;
    }

    public static Node getChildElement(Node parent) {
        NodeList childNodes = parent.getChildNodes();

        Node foundNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                foundNode = childNode;
                break;
            }
        }
        return foundNode;
    }

    public static Node getChildElement(Node parent, String elementName) {
        NodeList childNodes = parent.getChildNodes();

        Node foundNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (elementName.equals(childNode.getLocalName())) {
                foundNode = childNode;
                break;
            }
        }
        if (foundNode == null) {
            throw new ClientFaultException(INVALID_SOAP_REQUEST);
        }
        return foundNode;
    }
}
