/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;



/**
 * SOAP Server component. Incapsulates proxy to com.oanda.fxtrade.api functionality
 * @author andry
 *
 */
public class SoapServer {

	private MulticlientConnectionServer connectionServer = null;

	public static final int REQUEST_PORT  = 18081;
	public static final String STOP_COMMAND = "STOP";
	public static boolean gui = true;

	public SoapServer() {

		connectionServer = new MulticlientConnectionServer(
														new MessageStreamFactory(),
														new ProtocolResolverFactory(),
														new ConnectionAcceptor(REQUEST_PORT),
														new ThreadPool());
		if(gui) {
			Logger.isEnabled = true;
			new MainView(this);
		}

		Logger.getInstance().log("Start socket server...");
		connectionServer.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerStopper serverStopper = new ServerStopper();

		//check cmd line arguments
		if(isNoguiCommand(args)) {
			gui = false;
		}

		if(isDebugCommand(args)) {
			Logger.isEnabled = true;
		}

		if(isStopCommand(args)) {
			serverStopper.stopServer();
			System.exit(1);
		}

		if(serverStopper.isServerDetected()) {
			System.out.println("Another instance of SOAP server is detected.");
			System.exit(1);
		}

		SoapServer server = new SoapServer();

		server.waitForExitCommand();
		System.exit(1);
	}


	private static boolean isNoguiCommand(String[] args) {
		for(String arg : args) {
			if(arg.equals("nogui")) {
				return true;
			}
		}
		return false;
	}

	private static boolean isDebugCommand(String[] args) {
		for(String arg : args) {
			if(arg.equals("debug")) {
				return true;
			}
		}
		return false;
	}

	private static boolean isStopCommand(String[] args) {
		for(String arg : args) {
			if(arg.equals("stop")) {
				return true;
			}
		}
		return false;
	}

	void waitForExitCommand() {
		try {
			connectionServer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Logger getLogger() {
		return Logger.getInstance();
	}

	public MulticlientConnectionServer getConnectionServer() {
		return connectionServer;
	}
}
