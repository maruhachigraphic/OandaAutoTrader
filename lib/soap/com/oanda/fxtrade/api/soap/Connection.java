/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {

	private Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
	}

	public void close() throws IOException {
		socket.close();
	}

	public PrintWriter makeWriter() throws IOException {
		return new PrintWriter(socket.getOutputStream(), true);
	}

	public BufferedReader makeReader() throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public String getClientIP() {
		return socket.getInetAddress().getHostName() + " : " + socket.getInetAddress().getHostAddress();
	}
}
