/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.IOException;

public class MulticlientConnectionServer extends Thread {

	public interface CountListener {
		void inform(int connectionCount);

		void addClientHost(String clientIP);
	}

	private boolean isStopRequested = false;

	private ConnectionAcceptor connectionAcceptor;

	private Object lock = new Object();
	private MessageStreamFactory messageStreamFactory;
	private ProtocolResolverFactory protocolResolverFactory;

	private ThreadPool threadPool;

	//private CountListener countListener;

	public MulticlientConnectionServer(
			MessageStreamFactory streamFactory,
			ProtocolResolverFactory protocolFactory,
			ConnectionAcceptor connectionAcceptor,
			ThreadPool threadPool) {

		this.messageStreamFactory    = streamFactory;
		this.protocolResolverFactory = protocolFactory;

		this.connectionAcceptor = connectionAcceptor;
		this.connectionAcceptor.initialize();

		this.threadPool = threadPool;
	}

	public void run() {
		synchronized(lock) {
			while(isStopRequested == false) {


				try {
					Connection connection = connectionAcceptor.accept();
		        	Logger.getInstance().log(getClass().getName() + "::(New connectoin accepted)");

					if(isStopRequested) {
						continue;
					}
					addMessageServerForConnection(connection);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void addMessageServerForConnection(Connection connection) {
		MessageServer messageServer = new MessageServer(
				messageStreamFactory.create(connection),
				protocolResolverFactory.create());

		//countListener.addClientHost(connection.getClientIP());
		String uid = threadPool.add(messageServer);
		messageServer.setDestroyHandler(threadPool, uid);
	}

	/**
	 * only for testing purposes. will be good to control thread pool
	 * @return
	 */
	int getConnectionCount() {
		return threadPool.size();
	}

	public void shutDown() {
		isStopRequested = true;
		connectionAcceptor.shutDown();

		synchronized(lock) {
			//just waiting for thread function to finish
		}

		threadPool.stopAll();
	}

	public void setCountListener(CountListener countListener) {
		//this.countListener = countListener;
		threadPool.setCountListener(countListener);
	}
}
