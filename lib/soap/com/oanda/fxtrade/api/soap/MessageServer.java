/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

public class MessageServer implements Runnable, DestroyHandler {

	private MessageStream    messageStream    = null;
	private ProtocolResolver protocolResolver = null;
	private DestroyHandler   destroyHandler   = null;
	private String           destroyUid;
	private Object           lock = new Object();

	public MessageServer(MessageStream messageStream, ProtocolResolver protocolResolver) {
		this.messageStream    = messageStream;
		this.protocolResolver = protocolResolver;
		this.protocolResolver.setLogoutCommandHandler(this);
	}

	public void run() {
		try {
			synchronized (lock) {
				String request = messageStream.readNext();
	            messageStream.write(protocolResolver.resolve(request));
			}
		}
		catch (Exception e) {
			Logger.getInstance().log("Lost connection. Closing server");
			protocolResolver.resolve(null);//destroy
		}
	}

	public void onDestroy(String uid) {
		synchronized (lock) {
			destroyHandler.onDestroy(destroyUid);
		}
	}

	public void setDestroyHandler(DestroyHandler destroyHandler, String uid) {
		this.destroyHandler = destroyHandler;
		this.destroyUid     = uid;
	}

	public void onShutdown() {
		messageStream.close();
	}
}
