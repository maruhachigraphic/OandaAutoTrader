/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.Arrays;


public class DefaultProtocolResolver implements ProtocolResolver {
	private Session session = new Session();
    private CommandRequestHandler functionHandler = new CommandRequestHandler(session);
	private DestroyHandler destroyHandler;

	// Cache previous SOAP requests and handler responses in order to reduce the number of SoapMessage objects being created
	private String cachedMessageString = null;
	private SoapMessage cachedSoapMessage = null;
	private String[] cachedResponse = null;
	private String cachedResponseString = null;

    public String resolve(String request) {

    	SoapMessage soapRequest = null;
        String functionName     = null;
        String[] parameters     = null;

        if((request != null) && !request.isEmpty()) {
            if (request.equals(cachedMessageString) && cachedSoapMessage != null) {
                soapRequest = cachedSoapMessage;
            }
            else {
                soapRequest = new SoapMessage(request);
                cachedMessageString = request;
                cachedSoapMessage = soapRequest;
            }
            functionName = soapRequest.getFunctionName();
            parameters = soapRequest.getParameters();
        }
        else {
            functionName = Command.DESTROY;
        }

        String[] handlerResponse = null;

        try {
        	handlerResponse = functionHandler.handle(functionName, parameters);
        }
        catch(Exception e) {
        	handlerResponse = new String[2];
        	handlerResponse[0] = "error";
        	handlerResponse[1] = e.toString();
    		Logger.getInstance().log(e.toString());
        }

        if(functionName.equals(Command.DESTROY)) {
            destroyHandler.onDestroy("");
        }

        if (!Arrays.equals(handlerResponse, cachedResponse) || cachedResponseString == null) {
            SoapMessage soapResponse = new SoapMessage(functionName, handlerResponse);
            cachedResponse = handlerResponse;
            cachedResponseString = soapResponse.toString();
        }

        return cachedResponseString;
    }

	public void shutDown() {
	}

	public void setLogoutCommandHandler(
			DestroyHandler logoutHandler) {
		this.destroyHandler = logoutHandler;
		this.session.setLogoutHandler(logoutHandler);
	}
}
