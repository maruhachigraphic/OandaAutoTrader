/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Iterator;

import com.oanda.fxtrade.api.soap.MulticlientConnectionServer.CountListener;

public class ThreadPool extends HashMap<String, ThreadRunner> implements DestroyHandler {
	private static final long serialVersionUID = 1865115594294346482L;

	private CountListener countListener = null;

	public String add(MessageServer runnable) {
		String uid = new UID().toString();
    	Logger.getInstance().log(getClass().getName() + "::(Adding uid: '" + uid + "')");
		put(uid, new ThreadRunner(runnable));

		informCountListener();
		return uid;
	}

	public void onDestroy(String uid) {
    	Logger.getInstance().log(getClass().getName() + "::(Removing uid: '" + uid + "' by destroy request)");

    	get(uid).shutdown();
		remove(uid);
		informCountListener();
	}
	public void setCountListener(CountListener countListener) {
		this.countListener = countListener;

	}

	private void informCountListener() {
		if(countListener != null) {
			countListener.inform(size());
		}
	}

	public void stopAll() {
		Iterator<String> it = keySet().iterator();
		while(it.hasNext()) {
			get(it.next()).shutdown();
		}
	}

}
