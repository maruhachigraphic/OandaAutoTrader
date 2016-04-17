/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServerStopper {

	public void stopServer() {
		Socket clientSocket = null;
	    PrintStream output = null;
	    try {
           clientSocket = new Socket("localhost", SoapServer.REQUEST_PORT);
	       output = new PrintStream(clientSocket.getOutputStream());
	       output.println(SoapServer.STOP_COMMAND);

	       output.close();
	       clientSocket.close();
	    }
	    catch (IOException e) {
	    }

	}

	public boolean isServerDetected() {
		Socket client = null;
	    try {
           client = new Socket("localhost", SoapServer.REQUEST_PORT);
           client.close();
	    }
	    catch (IOException e) {
	        return false;
	    }

		return true;
	}

}
