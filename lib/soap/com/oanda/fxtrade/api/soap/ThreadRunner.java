/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public class ThreadRunner extends Thread {

	private MessageServer runnable;
	private boolean       isStopRequested = false;
	private Object        lock            = new Object();

	public ThreadRunner(MessageServer runnable) {
		super();
		this.runnable = runnable;
		start();
	}

	@Override
	public void run() {
		synchronized(lock) {
			while(isStopRequested == false) {
	            try {
	    			runnable.run();
					sleep(1);
				} catch (InterruptedException e) {
					//it's ok
				}
			}
			runnable.onShutdown();
		}
	}

	public void shutdown() {
		interrupt();
		isStopRequested = true;
		synchronized(lock) {
		}
	}
}
