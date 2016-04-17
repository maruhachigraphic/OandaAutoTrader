/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import com.oanda.fxtrade.api.FXAccountEventInfo;
import com.oanda.fxtrade.api.FXEventInfo;
import com.oanda.fxtrade.api.FXRateEventInfo;

public class Events {

	public interface Record {
		public Vector<String> toStringArray();
	}

	public class AccountEventRecord implements Record {
		public String    id    = null;
		public FXAccountEventInfo eventInfo = null;

		public AccountEventRecord(String id, FXAccountEventInfo eventInfo) {
			this.id    = id;
			this.eventInfo = eventInfo;
		}

		public Vector<String> toStringArray() {
			Vector<String> result = new Vector<String>();
			result.add(id);
			result.add(Long.toString(eventInfo.getTransaction().getTransactionNumber()));
			result.add(		            eventInfo.getTransaction().getType             () );
			result.add(Long   .toString(eventInfo.getTransaction().getUnits            ()));
			result.add(Long   .toString(eventInfo.getTransaction().getTimestamp        ()));
			result.add(Double .toString(eventInfo.getTransaction().getPrice            ()));
			result.add(                 eventInfo.getTransaction().getPair().getBase   () );
			result.add(                 eventInfo.getTransaction().getPair().getQuote  () );
			result.add(Long.toString(eventInfo.getTransaction().getTransactionLink  ()));
			result.add(Long.toString(eventInfo.getTransaction().getDiaspora         ()));
			result.add(Double .toString(eventInfo.getTransaction().getBalance          ()));
			result.add(Double .toString(eventInfo.getTransaction().getInterest         ()));
			result.add(Integer.toString(eventInfo.getTransaction().getCompletionCode   ()));
			result.add(Double .toString(eventInfo.getTransaction().getStopLoss         ()));
			result.add(Double .toString(eventInfo.getTransaction().getTakeProfit       ()));
			result.add(Double .toString(eventInfo.getTransaction().getAmount           ()));
			result.add(Double .toString(eventInfo.getTransaction().getMargin           ()));
			result.add(Double .toString(eventInfo.getTransaction().getTrailingStopLoss ()));
			result.add(Double .toString(eventInfo.getTransaction().getPL               ()));

			return result;
		}
	}


	class RateEventRecord implements Record {
		public String    id    = null;
		public FXRateEventInfo eventInfo = null;
		public RateEventRecord(String id, FXRateEventInfo eventInfo) {
			this.id    = id;
			this.eventInfo = eventInfo;
		}
		public Vector<String> toStringArray() {
			Vector<String> result = new Vector<String>();

			result.add(id);
			result.add(                eventInfo.getPair().toString());
			result.add(Long  .toString(eventInfo.getTimestamp     ()));
			result.add(Double.toString(eventInfo.getTick().getBid ()));
			result.add(Double.toString(eventInfo.getTick().getAsk ()));

			return result;
		}
	}

	class SessionEventRecord implements Record {
		private String id      = null;
		private String message = "";
		public SessionEventRecord(String id, SessionEventInfo eventInfo) {
			this.id      = id;
			this.message = eventInfo.getEventText();
		}
		public Vector<String> toStringArray() {
			Vector<String> result = new Vector<String>();

			result.add(id     );
			result.add(message);

			return result;
		}
	}

    class EventList {
        private Queue<Record> events = new LinkedList<Record>();

        public void add(String id, FXEventInfo eventInfo, String type) {
        	if(type == "RATE_EVENT") {
        		events.add(new RateEventRecord(id, (FXRateEventInfo)eventInfo));
        	}
        	else if(type == "ACCOUNT_EVENT") {
        		events.add(new AccountEventRecord(id, (FXAccountEventInfo)eventInfo));
        	}
        	else {
        		events.add(new SessionEventRecord(id, (SessionEventInfo)eventInfo));
        	}
        }

        public Record remove() {
            return events.remove();
        }

        public boolean isEmpty() {
             return events.isEmpty();
        }

        public int size() {
        	return events.size();
        }

		public void clear() {
			events.clear();
		}
    }

    private EventList events = new EventList();
    private Object lock = new Object();

    public Record getNext() {
        synchronized(lock) {
            return events.remove();
        }
    }

    public void add(String uid, FXEventInfo eventInfo, String type) {
        synchronized(lock) {
            events.add(uid, eventInfo, type);
        }
    }

    public boolean hasMore() {
        synchronized(lock) {
            if(!events.isEmpty()) {
            	return true;
            }
        }
        return false;
    }

    public int size() {
    	return events.size();
    }

	public void clear() {
		events.clear();
	}
}
