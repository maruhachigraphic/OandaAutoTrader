/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SoapMessage {

    private String functionName = "";
    private Vector<String> parameters = new Vector<String>();
    private SOAPMessage message = null;

    public SoapMessage(String text) {

        try {
            createMessage();
            fromString(text);

        } catch (SOAPException e1) {
            e1.printStackTrace();
        }
    }

    public SoapMessage(String functionName_, String[] arguments) {
        try {
            createMessage();
            if(functionName_ != null) {
            	addFunctionCall(functionName_, arguments);
            }

        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    private void createMessage() throws SOAPException {
        MessageFactory messageFactory = null;
        messageFactory = MessageFactory.newInstance();
        message = messageFactory.createMessage();
    }

    public String getFunctionName() {
        return functionName;
    }

    private void fromString(String text) {
        SOAPPart sp = message.getSOAPPart();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document doc = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(text)));
            DOMSource domSource = new DOMSource(doc);
            sp.setContent(domSource);
            message.saveChanges();

            extractFunction();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            Logger.getInstance().log("SoapMessage.fromString() xml parsing error: '" + text + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    private void extractFunction() {
        SOAPPart sp = message.getSOAPPart();
        SOAPEnvelope se = null;
        try {
            se = sp.getEnvelope();
            SOAPBody sb = se.getBody();

            Iterator<?> it = sb.getChildElements();
            SOAPBodyElement bodyElement = (SOAPBodyElement) it.next();

            functionName = bodyElement.getLocalName();
            Iterator<?> paramIt = bodyElement.getChildElements();
            SOAPBodyElement paramElement = null;
            while (paramIt.hasNext()) {
                paramElement = (SOAPBodyElement) paramIt.next();
                parameters.add(paramElement.getValue());
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        OutputStream stringBuffer = new ByteArrayOutputStream();
        try {
            message.writeTo(stringBuffer);
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public void addFunctionCall(String functionName_, String [] parameters) {
        try {
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPBody body = envelope.getBody();

            if(functionName_.equals("")) {
            	functionName_ = "EmptyCommand";
            }
            SOAPElement bodyElement = body.addChildElement(envelope.createName(functionName_, "API", "oanda.fxtrade.api"));

            if(parameters != null) {
	            for(int index = 0; index < parameters.length; ++index) {
	                if(parameters[index] != null) {
	                    bodyElement.addChildElement("parameter").addTextNode(parameters[index]);
	                }
	            }
            }
            message.saveChanges();

            extractFunction();
        }
        catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    public String[] getParameters() {
        String[] array = new String[parameters.size()];
        parameters.toArray(array);
        return array;
    }
}
