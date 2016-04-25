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
package net.sf.jaceko.mock.service;

import com.centeractive.ws.SoapContext;
import com.centeractive.ws.SoapMessageBuilder;
import com.centeractive.ws.WsdlUtils;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.text.MessageFormat.format;

public class WsdlProcessor {
    private static final Logger LOG = Logger.getLogger(WsdlProcessor.class);

    private WSDLFactory factory;

    public WsdlProcessor() {
        try {
            factory = WSDLFactory.newInstance();
        } catch (WSDLException e) {
            LOG.error("WSDL reader initialization error, ", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<WebserviceOperation> getOperationsFromWsdl(final String wsdlFileName, final String fileText) {
        final List<WebserviceOperation> mockOperations = newArrayList();

        WSDLReader wsdlReader = factory.newWSDLReader();
        try {
            Definition def = wsdlReader.readWSDL(null, new InputSource(new StringReader(fileText)));
            SoapMessageBuilder soapMessageBuilder = new SoapMessageBuilder(def, fileText);
            Map<QName, Binding> bindingsMap = def.getBindings();
            Collection<Binding> bindings = bindingsMap.values();
            for (Binding binding : bindings) {
                if (WsdlUtils.isSoapBinding(binding)) {
                    List<BindingOperation> bindingOperations = binding.getBindingOperations();
                    for (BindingOperation bindingOperation : bindingOperations) {
                        String requestElementName = null;
                        Operation operation = bindingOperation.getOperation();
                        if (WsdlUtils.isRpc(binding)) {
                            requestElementName = operation.getName();
                        } else {
                            Map<String, Part> parts = operation.getInput().getMessage().getParts();
                            Part value = parts.entrySet().iterator().next().getValue();
                            requestElementName = value.getElementName().getLocalPart();
                        }


                        final WebserviceOperation mockOperation = new WebserviceOperation();
                        mockOperation.setOperationName(requestElementName);

                        SoapContext context = SoapContext.builder().alwaysBuildHeaders(false).build();
                        String responseMessage = soapMessageBuilder.buildSoapMessageFromOutput(binding, bindingOperation, context);
                        mockOperation.setDefaultResponseText(responseMessage);
                        mockOperations.add(mockOperation);
                    }
                    //only first soap binding is processed
                    break;
                }
            }
        } catch (WSDLException e) {
            LOG.error(format("error processing WSDL file: {0}, {1}", wsdlFileName, e.getMessage()));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mockOperations;
    }

}
