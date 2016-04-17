/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.FXEvent;

/**
 *	Session class holds SOAP session state from the moment of "create" command all the way
 *	to "destroy". It is a mediator storage for events that are dispatched from API
 *	events generating threads.
 *
 */
public class Session implements Observer {
	protected FXClient fxClient        = null;
	private Events events              = new Events();
	private DefaultApi apiFactory      = new DefaultApi();
	private HashMap<String, FXEvent>
					         listeners = new HashMap<String, FXEvent>();
	private String observerUid;
	//private DestroyHandler logoutHandler;

	public FXClient getClient() throws Exception {
		if(fxClient == null) {
			throw new Exception("FXClient is not created");
		}
		return fxClient;
	}

	public void createGameClient() {
		fxClient = apiFactory.createFXGame();
		fxClient.addObserver(this);
	}

	public void createTradeClient() {
		fxClient = apiFactory.createFXTrade();
		fxClient.addObserver(this);
	}

	public Events getEvents() {
		return events;
	}

	public ApiFactory getApiFactory() {
		return apiFactory;
	}

	public FXEvent findMappedListener(String listenerUid) {
		return listeners.get(listenerUid);
	}

	public void mapListener(FXEvent eventListener, String listenerUid) {
		listeners.put(listenerUid, eventListener);
	}

	public void unmapListener(String listenerUid) {
		listeners.remove(listenerUid);
	}

	public Account findAccount(int accountId) throws Exception {
		Vector<?> accounts = getClient().getUser().getAccounts();

		for(int index = 0; index < accounts.size(); ++index) {
			if(((Account)accounts.elementAt(index)).getAccountId() == accountId) {
				return (Account)accounts.elementAt(index);
			}
		}
		return null;
	}

	public void cleanup() throws Exception {
		Set<String> keys = listeners.keySet();
		for (String key : keys) {
			removeListener(key);
		}
		listeners.clear();
		events.clear();
	}

	private void removeListener(String key) throws Exception {
		FXEvent event = listeners.get(key);

		getClient().getRateTable().getEventManager().remove(event);
		Vector<?> accounts = getClient().getUser().getAccounts();
		for(int index = 0; index < accounts.size(); ++index) {
			((Account)accounts.get(index)).getEventManager().remove(event);
		}
	}

	public void update(Observable source, Object status) {
		if (source == fxClient) {
			if (status.equals(FXClient.CONNECTED)) {
				events.add(observerUid, new SessionEventInfo("CONNECTED"), "SESSION_EVENT");
				Logger.getInstance().log(this.getClass().getName() + "::update: Session Logged in");
			}
			else if (status.equals(FXClient.DISCONNECTED)) {
				events.add(observerUid, new SessionEventInfo("DISCONNECTED"), "SESSION_EVENT");
				Logger.getInstance().log(this.getClass().getName() + "::update: Session Logged out");
			}
		}
	}

	public void setLogoutHandler(DestroyHandler logoutHandler) {
		//this.logoutHandler = logoutHandler;
	}

	public void addObserver(String uid) {
		observerUid = uid;
	}
}
