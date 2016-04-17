/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionAcceptor {

	public static class SocketAcceptUnblocker {

		public static void connect(int portNum) {
			Socket client = null;
		    try {
		           client = new Socket("localhost", portNum);
		           client.close();
		    }
		    catch (IOException e) {

		    }
		}
	}

	private ServerSocket serverSocket;
	private int portNum;

	public ConnectionAcceptor(int portNum) {
		this.portNum = portNum;
	}

	public Connection accept() throws IOException {
		return new Connection(serverSocket.accept());
	}

	public void shutDown() {
		try {
			SocketAcceptUnblocker.connect(portNum);

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		try {
			this.serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
