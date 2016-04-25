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
package net.sf.jaceko.mock.dom;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DocumentImpl implements Document {

    private Document document;
    private final String strXml;

    /**
     * @param xml - xml string to parse
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public DocumentImpl(String xml) throws ParserConfigurationException, SAXException, IOException {
        this(xml, false);
    }

    /**
     * @param xml            - xml string to parse
     * @param namespaceAware - specifies that the created document object will provide support for XML namespaces
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public DocumentImpl(String xml, boolean namespaceAware) throws ParserConfigurationException,
        SAXException, IOException {
        if (xml == null) {
            throw new IllegalArgumentException("xml cannot be null");
        }
        this.strXml = xml;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(namespaceAware);
        DocumentBuilder documentBuilder;
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

    }


    public String toString() {
        return strXml;
    }


    public String getNodeName() {
        return document.getNodeName();
    }


    public String getNodeValue() throws DOMException {

        return document.getNodeValue();
    }


    public void setNodeValue(String nodeValue) throws DOMException {
        document.setNodeValue(nodeValue);
    }


    public short getNodeType() {
        return document.getNodeType();
    }


    public Node getParentNode() {
        return document.getParentNode();
    }


    public NodeList getChildNodes() {
        return document.getChildNodes();
    }


    public Node getFirstChild() {
        return document.getFirstChild();
    }


    public Node getLastChild() {
        return document.getLastChild();
    }


    public Node getPreviousSibling() {
        return document.getPreviousSibling();
    }


    public Node getNextSibling() {

        return document.getNextSibling();
    }


    public NamedNodeMap getAttributes() {

        return document.getAttributes();
    }


    public Document getOwnerDocument() {

        return document.getOwnerDocument();
    }


    public Node insertBefore(Node newChild, Node refChild) throws DOMException {

        return document.insertBefore(newChild, refChild);
    }


    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {

        return document.replaceChild(newChild, oldChild);
    }


    public Node removeChild(Node oldChild) throws DOMException {

        return document.removeChild(oldChild);
    }


    public Node appendChild(Node newChild) throws DOMException {

        return document.appendChild(newChild);
    }


    public boolean hasChildNodes() {

        return document.hasChildNodes();
    }


    public Node cloneNode(boolean deep) {

        return document.cloneNode(deep);
    }


    public void normalize() {
        document.normalize();
    }


    public boolean isSupported(String feature, String version) {

        return document.isSupported(feature, version);
    }


    public String getNamespaceURI() {

        return document.getNamespaceURI();
    }


    public String getPrefix() {

        return document.getPrefix();
    }


    public void setPrefix(String prefix) throws DOMException {
        document.setPrefix(prefix);
    }


    public String getLocalName() {
        return document.getLocalName();
    }


    public boolean hasAttributes() {

        return document.hasAttributes();
    }


    public String getBaseURI() {

        return document.getBaseURI();
    }


    public short compareDocumentPosition(Node other) throws DOMException {

        return document.compareDocumentPosition(other);
    }


    public String getTextContent() throws DOMException {

        return document.getTextContent();
    }


    public void setTextContent(String textContent) throws DOMException {
        document.setTextContent(textContent);

    }


    public boolean isSameNode(Node other) {

        return document.isSameNode(other);
    }


    public String lookupPrefix(String namespaceURI) {

        return document.lookupPrefix(namespaceURI);
    }


    public boolean isDefaultNamespace(String namespaceURI) {

        return document.isDefaultNamespace(namespaceURI);
    }


    public String lookupNamespaceURI(String prefix) {

        return document.lookupNamespaceURI(prefix);
    }


    public boolean isEqualNode(Node arg) {

        return document.isEqualNode(arg);
    }


    public Object getFeature(String feature, String version) {

        return document.getFeature(feature, version);
    }


    public Object setUserData(String key, Object data, UserDataHandler handler) {

        return document.setUserData(key, data, handler);
    }


    public Object getUserData(String key) {

        return document.getUserData(key);
    }


    public DocumentType getDoctype() {

        return document.getDoctype();
    }


    public DOMImplementation getImplementation() {

        return document.getImplementation();
    }


    public Element getDocumentElement() {

        return document.getDocumentElement();
    }


    public Element createElement(String tagName) throws DOMException {

        return document.createElement(tagName);
    }


    public DocumentFragment createDocumentFragment() {

        return document.createDocumentFragment();
    }


    public Text createTextNode(String data) {

        return document.createTextNode(data);
    }


    public Comment createComment(String data) {

        return document.createComment(data);
    }


    public CDATASection createCDATASection(String data) throws DOMException {

        return document.createCDATASection(data);
    }


    public ProcessingInstruction createProcessingInstruction(String target, String data)
        throws DOMException {

        return document.createProcessingInstruction(target, data);
    }


    public Attr createAttribute(String name) throws DOMException {

        return document.createAttribute(name);
    }


    public EntityReference createEntityReference(String name) throws DOMException {

        return document.createEntityReference(name);
    }


    public NodeList getElementsByTagName(String tagname) {

        return document.getElementsByTagName(tagname);
    }


    public Node importNode(Node importedNode, boolean deep) throws DOMException {

        return document.importNode(importedNode, deep);
    }


    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {

        return document.createElementNS(namespaceURI, qualifiedName);
    }


    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {

        return document.createAttributeNS(namespaceURI, qualifiedName);
    }


    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {

        return document.getElementsByTagNameNS(namespaceURI, localName);
    }


    public Element getElementById(String elementId) {

        return document.getElementById(elementId);
    }


    public String getInputEncoding() {

        return document.getInputEncoding();
    }


    public String getXmlEncoding() {

        return document.getInputEncoding();
    }


    public boolean getXmlStandalone() {

        return document.getXmlStandalone();
    }


    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        document.setXmlStandalone(xmlStandalone);

    }


    public String getXmlVersion() {

        return document.getXmlVersion();
    }


    public void setXmlVersion(String xmlVersion) throws DOMException {
        document.setXmlVersion(xmlVersion);

    }


    public boolean getStrictErrorChecking() {

        return document.getStrictErrorChecking();
    }


    public void setStrictErrorChecking(boolean strictErrorChecking) {
        document.setStrictErrorChecking(strictErrorChecking);

    }


    public String getDocumentURI() {

        return document.getDocumentURI();
    }


    public void setDocumentURI(String documentURI) {
        document.setDocumentURI(documentURI);

    }


    public Node adoptNode(Node source) throws DOMException {

        return document.adoptNode(source);
    }


    public DOMConfiguration getDomConfig() {

        return document.getDomConfig();
    }


    public void normalizeDocument() {
        document.normalizeDocument();

    }


    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {

        return document.renameNode(n, namespaceURI, qualifiedName);
    }
}
