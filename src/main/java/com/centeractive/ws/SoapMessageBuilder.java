/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */
package com.centeractive.ws;

import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import javax.wsdl.*;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was extracted from the soapUI code base by centeractive ag in October 2011.
 * The main reason behind the extraction was to separate the code that is responsible
 * for the generation of the SOAP messages from the rest of the soapUI's code that is
 * tightly coupled with other modules, such as soapUI's graphical user interface, etc.
 * The goal was to create an open-source java project whose main responsibility is to
 * handle SOAP message generation and SOAP transmission purely on an XML level.
 * <br/>
 * centeractive ag would like to express strong appreciation to SmartBear Software and
 * to the whole team of soapUI's developers for creating soapUI and for releasing its
 * source code under a free and open-source licence. centeractive ag extracted and
 * modifies some parts of the soapUI's code in good faith, making every effort not
 * to impair any existing functionality and to supplement it according to our
 * requirements, applying best practices of software design.
 *
 * Changes done:
 * - changing location in the package structure
 * - removal of dependencies and code parts that are out of scope of SOAP message generation
 * - minor fixes to make the class compile out of soapUI's code base
 * - rename to SoapBuilder (from SoapMessageBuilder)
 * - slight reorganization of the public API and arguments of the methods
 * - introduction of OperationWrapper and SoapContext classes
 * - addition of saveWSDL and createAndSave methods
 */

/**
 * Builds SOAP requests according to WSDL/XSD definitions
 *
 * @author Ole.Matzura
 */
@SuppressWarnings("unchecked")
public class SoapMessageBuilder {

    private final static Logger log = Logger.getLogger(SoapMessageBuilder.class);

    private Definition definition;
    private SchemaDefinitionWrapper definitionWrapper;

    // ----------------------------------------------------------
    // Constructors and factory methods
    // ----------------------------------------------------------

    /**
     * @param wsdlUrl url of the wsdl to import
     * @throws WSDLException thrown in case of import errors
     */
    public SoapMessageBuilder(URL wsdlUrl) throws WSDLException {
        WSDLReader reader = new WSDLReaderImpl();
        reader.setFeature("javax.wsdl.verbose", false);
        this.definition = reader.readWSDL(wsdlUrl.toString());
        this.definitionWrapper = new SchemaDefinitionWrapper(definition, wsdlUrl.toString());
    }

    public SoapMessageBuilder(Definition definition, String xml) {
        super();
        this.definition = definition;
        this.definitionWrapper = new SchemaDefinitionWrapper(definition, "http://localhost:8080/mock", xml);
    }

    // ----------------------------------------------------------
    // EMPTY MESSAGE GENERATORS
    // ----------------------------------------------------------
    public String buildEmptyMessage(QName bindingQName, SoapContext context) {
        return buildEmptyMessage(getSoapVersion(getBindingByName(bindingQName)), context);
    }

    public String buildEmptyMessage(Binding binding, SoapContext context) {
        return buildEmptyMessage(getSoapVersion(binding), context);
    }

    public static String buildEmptyMessage(SoapVersion soapVersion, SoapContext context) {
        SampleXmlUtil generator = new SampleXmlUtil(false, context);
        return generator.createSample(soapVersion.getEnvelopeType());
    }

    // ----------------------------------------------------------
    // INPUT MESSAGE GENERATORS
    // ----------------------------------------------------------
    public String buildSoapMessageFromInput(Binding binding, BindingOperation bindingOperation, SoapContext context)
        throws Exception {
        SoapVersion soapVersion = getSoapVersion(binding);
        boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(bindingOperation);
        SampleXmlUtil xmlGenerator = new SampleXmlUtil(inputSoapEncoded, context);

        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        cursor.toNextToken();
        cursor.beginElement(soapVersion.getEnvelopeQName());

        if (inputSoapEncoded) {
            cursor.insertNamespace("xsi", Constants.XSI_NS);
            cursor.insertNamespace("xsd", Constants.XSD_NS);
        }

        cursor.toFirstChild();
        cursor.beginElement(soapVersion.getBodyQName());
        cursor.toFirstChild();

        if (WsdlUtils.isRpc(definition, bindingOperation)) {
            buildRpcRequest(bindingOperation, soapVersion, cursor, xmlGenerator);
        } else {
            buildDocumentRequest(bindingOperation, cursor, xmlGenerator);
        }

        if (context.isAlwaysBuildHeaders()) {
            BindingInput bindingInput = bindingOperation.getBindingInput();
            if (bindingInput != null) {
                List<?> extensibilityElements = bindingInput.getExtensibilityElements();
                List<WsdlUtils.SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders(extensibilityElements);
                addHeaders(soapHeaders, soapVersion, cursor, xmlGenerator);
            }
        }
        cursor.dispose();

        try {
            StringWriter writer = new StringWriter();
            XmlUtils.serializePretty(object, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return object.xmlText();
        }
    }

    // ----------------------------------------------------------
    // OUTPUT MESSAGE GENERATORS
    // ----------------------------------------------------------
    public String buildSoapMessageFromOutput(Binding binding, BindingOperation bindingOperation, SoapContext context)
        throws Exception {
        boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(bindingOperation);
        SampleXmlUtil xmlGenerator = new SampleXmlUtil(inputSoapEncoded, context);
        SoapVersion soapVersion = getSoapVersion(binding);

        XmlObject object = XmlObject.Factory.newInstance();
        XmlCursor cursor = object.newCursor();
        cursor.toNextToken();
        cursor.beginElement(soapVersion.getEnvelopeQName());

        if (inputSoapEncoded) {
            cursor.insertNamespace("xsi", Constants.XSI_NS);
            cursor.insertNamespace("xsd", Constants.XSD_NS);
        }

        cursor.toFirstChild();

        cursor.beginElement(soapVersion.getBodyQName());
        cursor.toFirstChild();

        if (WsdlUtils.isRpc(definition, bindingOperation)) {
            buildRpcResponse(bindingOperation, soapVersion, cursor, xmlGenerator);
        } else {
            buildDocumentResponse(bindingOperation, cursor, xmlGenerator);
        }

        if (context.isAlwaysBuildHeaders()) {
            // bindingOutput will be null for one way operations,
            // but then we shouldn't be here in the first place???
            BindingOutput bindingOutput = bindingOperation.getBindingOutput();
            if (bindingOutput != null) {
                List<?> extensibilityElements = bindingOutput.getExtensibilityElements();
                List<WsdlUtils.SoapHeader> soapHeaders = WsdlUtils.getSoapHeaders(extensibilityElements);
                addHeaders(soapHeaders, soapVersion, cursor, xmlGenerator);
            }
        }
        cursor.dispose();

        try {
            StringWriter writer = new StringWriter();
            XmlUtils.serializePretty(object, writer);
            return writer.toString();
        } catch (Exception e) {
            log.warn("Exception during message generation", e);
            return object.xmlText();
        }
    }

    // ----------------------------------------------------------
    // UTILS
    // ----------------------------------------------------------
    public Definition getDefinition() {
        return definition;
    }

    public SchemaDefinitionWrapper getSchemaDefinitionWrapper() {
        return definitionWrapper;
    }

    public BindingOperation getOperationByName(QName bindingName, String operationName, String operationInputName,
                                               String operationOutputName) {
        Binding binding = getBindingByName(bindingName);
        if (binding == null) {
            return null;
        }
        BindingOperation operation = binding.getBindingOperation(operationName, operationInputName, operationOutputName);
        if (operation == null) {
            throw new SoapBuilderException("Operation not found");
        }
        return operation;
    }

    public Binding getBindingByName(QName bindingName) {
        Binding binding = this.definition.getBinding(bindingName);
        if (binding == null) {
            throw new SoapBuilderException("Binding not found");
        }
        return binding;
    }

    public List<QName> getBindingNames() {
        return new ArrayList<QName>(definition.getAllBindings().keySet());
    }

    // --------------------------------------------------------------------------
    // Internal methods - END OF PUBLIC API
    // --------------------------------------------------------------------------
    private static SoapVersion getSoapVersion(Binding binding) {
        SoapVersion soapVersion = WsdlUtils.getSoapVersion(binding);
        if (soapVersion == null) {
            throw new SoapBuilderException("SOAP binding not recognized");
        }
        return soapVersion;
    }

    private void addHeaders(List<WsdlUtils.SoapHeader> headers, SoapVersion soapVersion, XmlCursor cursor,
                            SampleXmlUtil xmlGenerator) throws Exception {
        // reposition
        cursor.toStartDoc();
        cursor.toChild(soapVersion.getEnvelopeQName());
        cursor.toFirstChild();

        cursor.beginElement(soapVersion.getHeaderQName());
        cursor.toFirstChild();

        for (int i = 0; i < headers.size(); i++) {
            WsdlUtils.SoapHeader header = headers.get(i);

            Message message = definition.getMessage(header.getMessage());
            if (message == null) {
                log.error("Missing message for header: " + header.getMessage());
                continue;
            }

            Part part = message.getPart(header.getPart());

            if (part != null)
                createElementForPart(part, cursor, xmlGenerator);
            else
                log.error("Missing part for header; " + header.getPart());
        }
    }

    private void buildDocumentResponse(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtil xmlGenerator)
        throws Exception {
        Part[] parts = WsdlUtils.getOutputParts(bindingOperation);

        for (int i = 0; i < parts.length; i++) {
            Part part = parts[i];

            if (!WsdlUtils.isAttachmentOutputPart(part, bindingOperation)
                && (part.getElementName() != null || part.getTypeName() != null)) {
                XmlCursor c = cursor.newCursor();
                c.toLastChild();
                createElementForPart(part, c, xmlGenerator);
                c.dispose();
            }
        }
    }

    private void buildDocumentRequest(BindingOperation bindingOperation, XmlCursor cursor, SampleXmlUtil xmlGenerator)
        throws Exception {
        Part[] parts = WsdlUtils.getInputParts(bindingOperation);

        for (int i = 0; i < parts.length; i++) {
            Part part = parts[i];
            if (!WsdlUtils.isAttachmentInputPart(part, bindingOperation)
                && (part.getElementName() != null || part.getTypeName() != null)) {
                XmlCursor c = cursor.newCursor();
                c.toLastChild();
                createElementForPart(part, c, xmlGenerator);
                c.dispose();
            }
        }
    }

    private void createElementForPart(Part part, XmlCursor cursor, SampleXmlUtil xmlGenerator) throws Exception {
        QName elementName = part.getElementName();
        QName typeName = part.getTypeName();

        if (elementName != null) {
            cursor.beginElement(elementName);

            if (definitionWrapper.hasSchemaTypes()) {
                SchemaGlobalElement elm = definitionWrapper.getSchemaTypeLoader().findElement(elementName);
                if (elm != null) {
                    cursor.toFirstChild();
                    xmlGenerator.createSampleForType(elm.getType(), cursor);
                } else
                    log.error("Could not find element [" + elementName + "] specified in part [" + part.getName() + "]");
            }

            cursor.toParent();
        } else {
            // cursor.beginElement( new QName(
            // wsdlContext.getWsdlDefinition().getTargetNamespace(),
            // part.getName()
            // ));
            cursor.beginElement(part.getName());
            if (typeName != null && definitionWrapper.hasSchemaTypes()) {
                SchemaType type = definitionWrapper.getSchemaTypeLoader().findType(typeName);

                if (type != null) {
                    cursor.toFirstChild();
                    xmlGenerator.createSampleForType(type, cursor);
                } else
                    log.error("Could not find type [" + typeName + "] specified in part [" + part.getName() + "]");
            }

            cursor.toParent();
        }
    }

    private void buildRpcRequest(BindingOperation bindingOperation, SoapVersion soapVersion, XmlCursor cursor,
                                 SampleXmlUtil xmlGenerator) throws Exception {
        // rpc requests use the operation name as root element
        String ns = WsdlUtils.getSoapBodyNamespace(bindingOperation.getBindingInput().getExtensibilityElements());
        if (ns == null) {
            ns = WsdlUtils.getTargetNamespace(definition);
            log.warn("missing namespace on soapbind:body for RPC request, using targetNamespace instead (BP violation)");
        }

        cursor.beginElement(new QName(ns, bindingOperation.getName()));
        // TODO
        if (xmlGenerator.isSoapEnc())
            cursor.insertAttributeWithValue(new QName(soapVersion.getEnvelopeNamespace(), "encodingStyle"),
                soapVersion.getEncodingNamespace());

        Part[] inputParts = WsdlUtils.getInputParts(bindingOperation);
        for (int i = 0; i < inputParts.length; i++) {
            Part part = inputParts[i];

            if (WsdlUtils.isAttachmentInputPart(part, bindingOperation)) {
                // TODO - generation of attachment flag could be externalized
                // if
                // (iface.getSettings().getBoolean(WsdlSettings.ATTACHMENT_PARTS))
                // {
                XmlCursor c = cursor.newCursor();
                c.toLastChild();
                c.beginElement(part.getName());
                c.insertAttributeWithValue("href", part.getName() + "Attachment");
                c.dispose();
                // }
            } else {
                if (definitionWrapper.hasSchemaTypes()) {
                    QName typeName = part.getTypeName();
                    if (typeName != null) {
                        // TODO - Don't know whether will work
                        // SchemaType type =
                        // wsdlContext.getInterfaceDefinition().findType(typeName);
                        SchemaType type = definitionWrapper.findType(typeName);

                        if (type != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(part.getName());
                            c.toPrevToken();

                            xmlGenerator.createSampleForType(type, c);
                            c.dispose();
                        } else
                            log.warn("Failed to find type [" + typeName + "]");
                    } else {
                        SchemaGlobalElement element = definitionWrapper.getSchemaTypeLoader().findElement(part.getElementName());
                        if (element != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(element.getName());
                            c.toPrevToken();

                            xmlGenerator.createSampleForType(element.getType(), c);
                            c.dispose();
                        } else
                            log.warn("Failed to find element [" + part.getElementName() + "]");
                    }
                }
            }
        }
    }

    private void buildRpcResponse(BindingOperation bindingOperation, SoapVersion soapVersion, XmlCursor cursor,
                                  SampleXmlUtil xmlGenerator) throws Exception {
        // rpc requests use the operation name as root element
        BindingOutput bindingOutput = bindingOperation.getBindingOutput();
        String ns = bindingOutput == null ? null : WsdlUtils.getSoapBodyNamespace(bindingOutput.getExtensibilityElements());

        if (ns == null) {
            ns = WsdlUtils.getTargetNamespace(definition);
            log.warn("missing namespace on soapbind:body for RPC response, using targetNamespace instead (BP violation)");
        }

        cursor.beginElement(new QName(ns, bindingOperation.getName() + "Response"));
        if (xmlGenerator.isSoapEnc())
            cursor.insertAttributeWithValue(new QName(soapVersion.getEnvelopeNamespace(), "encodingStyle"),
                soapVersion.getEncodingNamespace());

        Part[] inputParts = WsdlUtils.getOutputParts(bindingOperation);
        for (int i = 0; i < inputParts.length; i++) {
            Part part = inputParts[i];
            if (WsdlUtils.isAttachmentOutputPart(part, bindingOperation)) {
                // if( iface.getSettings().getBoolean(
                // WsdlSettings.ATTACHMENT_PARTS ) )
                {
                    XmlCursor c = cursor.newCursor();
                    c.toLastChild();
                    c.beginElement(part.getName());
                    c.insertAttributeWithValue("href", part.getName() + "Attachment");
                    c.dispose();
                }
            } else {
                if (definitionWrapper.hasSchemaTypes()) {
                    QName typeName = part.getTypeName();
                    if (typeName != null) {
                        SchemaType type = definitionWrapper.findType(typeName);

                        if (type != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(part.getName());
                            c.toPrevToken();

                            xmlGenerator.createSampleForType(type, c);
                            c.dispose();
                        } else
                            log.warn("Failed to find type [" + typeName + "]");
                    } else {
                        SchemaGlobalElement element = definitionWrapper.getSchemaTypeLoader().findElement(part.getElementName());
                        if (element != null) {
                            XmlCursor c = cursor.newCursor();
                            c.toLastChild();
                            c.insertElement(element.getName());
                            c.toPrevToken();

                            String defaultTextForElement = element.getDefaultText();
                            if (StringUtils.isNotEmpty(defaultTextForElement)) {
                                c.insertChars(defaultTextForElement);
                            } else {
                                xmlGenerator.createSampleForType(element.getType(), c);
                            }
                            c.dispose();
                        } else
                            log.warn("Failed to find element [" + part.getElementName() + "]");
                    }
                }
            }
        }
    }
}
