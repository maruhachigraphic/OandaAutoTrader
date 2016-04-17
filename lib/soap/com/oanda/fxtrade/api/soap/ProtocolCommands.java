/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import com.oanda.fxtrade.api.Account;
import com.oanda.fxtrade.api.CandlePoint;
import com.oanda.fxtrade.api.Configuration;
import com.oanda.fxtrade.api.FXEvent;
import com.oanda.fxtrade.api.FXHistoryPoint;
import com.oanda.fxtrade.api.FXPair;
import com.oanda.fxtrade.api.FXTick;
import com.oanda.fxtrade.api.Instrument;
import com.oanda.fxtrade.api.MarketOrder;
import com.oanda.fxtrade.api.MinMaxPoint;
import com.oanda.fxtrade.api.Position;
import com.oanda.fxtrade.api.TrailingStop;
import com.oanda.fxtrade.api.Transaction;
import com.oanda.fxtrade.api.User;

public class ProtocolCommands {

	public class GetTrailingStop extends Base {

		public GetTrailingStop(Session session) {
			super(session);
		}

		@Override
		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			int accountId = Integer.parseInt(commandArguments[0]);
			int tradeNumber = Integer.parseInt(commandArguments[1]);

			String[] result = null;
			try {
				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.MarketOrder marketOrder = account
						.getTradeWithId(tradeNumber);

				TrailingStop trailingStop = account.getTrailingStop(marketOrder);

				result = new String[8];

				result[0] = trailingStop.getPair().getBase();
				result[1] = trailingStop.getPair().getQuote();
				result[2] = "" + trailingStop.getTicketNumber();
				result[3] = "" + trailingStop.isBuy();
				result[4] = "" + trailingStop.getTrailingAmount();
				result[5] = "" + trailingStop.getTrailingAmountAsPrice();
				result[6] = "" + trailingStop.getCurrentValue();
				result[7] = "" + trailingStop.getCurrentValueTime();

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class GetAllSymbols extends Base {

		public GetAllSymbols(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			Logger.getInstance().log(getClass().getName() + "::execute()");
			String[] result = null;
			try {
				Collection<FXPair> pairs = session.getClient().getRateTable().getAllSymbols();
				result = new String[pairs.size() * 2];
				int index = 0;
				FXPair pair = null;
			    for ( Iterator<FXPair> iter = pairs.iterator(); iter.hasNext(); ) {
			    	pair = iter.next();
					result[index * 2]     = pair.getBase();
					result[index * 2 + 1] = pair.getQuote();
			        ++index;
			    }

			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class GetTransactions extends Base {

		public GetTransactions(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			Logger.getInstance().log(getClass().getName() + "::execute()");
			String[] result = null;
			try {
				Account account = session
						.getClient()
						.getUser()
						.getAccountWithId(Integer.parseInt(commandArguments[0]));
				Vector<?> transactions = account.getTransactions();
				result = new String[transactions.size()];
				for (int index = 0; index < transactions.size(); ++index) {
					Transaction transaction = (Transaction) transactions
							.get(index);
					String serializedTransaction = transaction.getAmount()
							+ "|" + transaction.getBalance() + "|"
							+ transaction.getCompletionCode() + "|"
							+ transaction.getDiaspora() + "|"
							+ transaction.getInterest() + "|"
							+ transaction.getMargin() + "|";

							if(transaction.getPair() == null) {
								serializedTransaction += "NUL|NUL|";
							}
							else {
								serializedTransaction +=  transaction.getPair().getBase() + "|"
								+ transaction.getPair().getQuote() + "|";
							}

							serializedTransaction += transaction.getPrice() + "|"
							+ transaction.getStopLoss() + "|"
							+ transaction.getTakeProfit() + "|"
							+ transaction.getTimestamp() + "|"
							+ transaction.getTransactionLink() + "|"
							+ transaction.getTransactionNumber() + "|"
							+ transaction.getType() + "|"
							+ transaction.getUnits() + "|"
                            + transaction.getTrailingStopLoss() + "|"
                            + transaction.getPL() + "|";

					result[index] = serializedTransaction;
				}

			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}

	}

	public class Create extends Base {

		public Create(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);
			String[] result = null;

			try {
				if(commandArguments[1] != null) {
					Configuration.setVersion(commandArguments[1]);
				}
				if(commandArguments[0].equals("FXGAME")) {
					session.createGameClient();
				} else {
					session.createTradeClient();
				}
			}
			catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class Destroy extends Base {

		public Destroy(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				session.cleanup();
			}
			catch (Exception e) {
				result = processException(e);
			}

			// even after a cleanup() exception, logout() must be called in order to let rate/event threads die
			// thus, logout() is placed in its own try-catch block
			try {
				session.getClient().logout();
			}
			catch (Exception e) {
				String[] logoutResult = processException(e);
				if (result != null) {
					result[1] += "\n" + logoutResult[1];
				}
				else {
					result = logoutResult;
				}
			}

			return result;
		}
	}

	public class GetAccountsList extends Base {

		public GetAccountsList(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Vector<?> accounts = null;
			String[] result = null;
			try {
				accounts = session.getClient().getUser().getAccounts();

				result = new String[accounts.size()];
				for (int index = 0; index < accounts.size(); ++index) {
					result[index] = Integer.toString(
							((Account) accounts.get(index)).getAccountId())
							.toString();
				}
			}
			catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class SetProfile extends Base {

		public SetProfile(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			User user = null;
			String[] result = null;
			try {
				user = session.getClient().getUser();
				user.setProfile(commandArguments[0]);
			}
			catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetUserInfo extends Base {

		public GetUserInfo(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			Logger.getInstance().log(getClass().getName() + "::execute()");
			User user = null;
			String[] result = null;
			try {
				user = session.getClient().getUser();

				result = new String[6];
				result[0] = Integer.toString(user.getUserId    ());
				result[1] =                  user.getName      () ;
				result[2] =                  user.getEmail     () ;
				result[3] =                  user.getAddress   () ;
				result[4] =                  user.getTelephone () ;
				result[5] =                  user.getProfile   () ;
			}
			catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetAccount extends Base {

		public GetAccount(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Account account = null;
			String[] result = null;
			try {
				account = session.getClient().getUser().getAccountWithId(
						new Integer(commandArguments[0]));

				result = new String[10];

				result[0] = commandArguments[0];
				result[1] = account.getAccountName();
				result[2] = Double.toString(account.getBalance());
				result[3] = account.getHomeCurrency();
				result[4] = Double.toString(account.getMarginAvailable());
				result[5] = Double.toString(account.getMarginUsed());
				result[6] = Double.toString(account.getMarginRate());
				result[7] = Double.toString(account.getMarginCallRate());
				result[8] = Double.toString(account.getRealizedPL());
				result[9] = Double.toString(account.getUnrealizedPL());
			}
			catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class CloseOrder extends Base {

		public CloseOrder(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			int accountId = Integer.parseInt(commandArguments[0]);
			int orderNumber = Integer.parseInt(commandArguments[1]);

			String[] result = null;
			try {
				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.LimitOrder limitOrder = account
						.getOrderWithId(orderNumber);

				account.close(limitOrder);
			}
			catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class ChangeOrder extends Base {

		public ChangeOrder(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;
			try {
				int    accountId       = Integer.parseInt   (commandArguments[0]);
				int    absOfUnits      = Integer.parseInt   (commandArguments[1]);
				double price           = Double .parseDouble(commandArguments[2]);
				double lowPriceLimit   = Double .parseDouble(commandArguments[3]);
				double highPriceLimit  = Double .parseDouble(commandArguments[4]);
				int    duration        = Integer.parseInt   (commandArguments[5]);
				double stopLossPrice   = Double .parseDouble(commandArguments[6]);
				double takeProfitPrice = Double .parseDouble(commandArguments[7]);
				int    orderNumber     = Integer.parseInt   (commandArguments[8]);

				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.LimitOrder limitOrder = account
						.getOrderWithId(orderNumber);

				limitOrder.setUnits(absOfUnits);
				limitOrder.setPrice(price);
				limitOrder.setLowPriceLimit(lowPriceLimit);
				limitOrder.setHighPriceLimit(highPriceLimit);
				limitOrder.setExpiry(duration);
				limitOrder.setStopLoss(session.getApiFactory()
						.createStopLossOrder(stopLossPrice));
				limitOrder.setTakeProfit(session.getApiFactory()
						.createTakeProfitOrder(takeProfitPrice));

				account.modify(limitOrder);

			}
			catch (Exception e) {
				result = processException(e);
			}
			return result;
		}

	}

	public class LimitOrder extends Base {

		public LimitOrder(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				int    accountId       = Integer.parseInt   (commandArguments[0]);
				String base            = commandArguments[1];
				String quote           = commandArguments[2];
				int    units           = Integer.parseInt   (commandArguments[3]);
				double price           = Double .parseDouble(commandArguments[4]);
				double lowPriceLimit   = Double .parseDouble(commandArguments[5]);
				double highPriceLimit  = Double .parseDouble(commandArguments[6]);
				int    duration        = Integer.parseInt   (commandArguments[7]);
				double stopLossPrice   = Double .parseDouble(commandArguments[8]);
				double takeProfitPrice = Double .parseDouble(commandArguments[9]);

				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.LimitOrder limitOrder = session
						.getApiFactory().createLimitOrder();

				limitOrder.setPair(session.getApiFactory().createFXPair(base,
						quote));
				limitOrder.setUnits(units);

				limitOrder.setPrice(price);
				limitOrder.setLowPriceLimit(lowPriceLimit);
				limitOrder.setHighPriceLimit(highPriceLimit);
				limitOrder.setExpiry(duration);
				limitOrder.setStopLoss(session.getApiFactory()
						.createStopLossOrder(stopLossPrice));
				limitOrder.setTakeProfit(session.getApiFactory()
						.createTakeProfitOrder(takeProfitPrice));

				account.execute(limitOrder);
				result = new String[4];
				result[0] = Long.toString(limitOrder.getTransactionNumber());
				result[1] = Double .toString(limitOrder.getPrice            ());
				result[2] = Long   .toString(limitOrder.getTimestamp        ());
				result[3] = Double .toString(limitOrder.getUnits            ());

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class CloseTrade extends Base {

		public CloseTrade(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			int accountId = Integer.parseInt(commandArguments[0]);
			int tradeNumber = Integer.parseInt(commandArguments[1]);

			String[] result = null;
			try {
				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.MarketOrder marketOrder = account
						.getTradeWithId(tradeNumber);

				account.close(marketOrder);
				com.oanda.fxtrade.api.MarketOrder closingOrder =
												marketOrder.getClose();

				String serializedMarketOrder = serializeMarketOrder(closingOrder);
				result = new String[1];
				result[0] = serializedMarketOrder;

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class ChangeTrade extends Base {

		public ChangeTrade(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			int accountId = Integer.parseInt(commandArguments[0]);
			int tradeNumber = Integer.parseInt(commandArguments[1]);
			double stopLossPrice = Double.parseDouble(commandArguments[2]);
			double takeProfitPrice = Double.parseDouble(commandArguments[3]);

			String[] result = null;
			try {
				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.MarketOrder marketOrder = account
						.getTradeWithId(tradeNumber);

				marketOrder.setStopLoss(session.getApiFactory()
						.createStopLossOrder(stopLossPrice));
				marketOrder.setTakeProfit(session.getApiFactory()
						.createTakeProfitOrder(takeProfitPrice));

				account.modify(marketOrder);
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class TradeOrder extends Base {

		public TradeOrder(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				int    accountId       = Integer.parseInt  (commandArguments[0]);
				String base            =                    commandArguments[1];
				String quote           =                    commandArguments[2];
				int    units           = Integer.parseInt  (commandArguments[3]);
				double lowPriceLimit   = Double.parseDouble(commandArguments[4]);
				double highPriceLimit  = Double.parseDouble(commandArguments[5]);
				double stopLossPrice   = Double.parseDouble(commandArguments[6]);
				double takeProfitPrice = Double.parseDouble(commandArguments[7]);
				double trailingStop    = Double.parseDouble(commandArguments[8]);

				Account account = session.getClient().getUser()
						.getAccountWithId(accountId);
				com.oanda.fxtrade.api.MarketOrder marketOrder = session
						.getApiFactory().createMarketOrder();

				marketOrder.setPair(session.getApiFactory().createFXPair(base,
						quote));
				marketOrder.setUnits(units);
				marketOrder.setLowPriceLimit(lowPriceLimit);
				marketOrder.setHighPriceLimit(highPriceLimit);
				marketOrder.setStopLoss(session.getApiFactory()
						.createStopLossOrder(stopLossPrice));
				marketOrder.setTakeProfit(session.getApiFactory()
						.createTakeProfitOrder(takeProfitPrice));
				marketOrder.setTrailingStopLoss(trailingStop);

				account.execute(marketOrder);
				result = new String[4];
				result[0] = Long
						.toString(marketOrder.getTransactionNumber  ());
				result[1] = Double.toString(marketOrder.getPrice    ());
				result[2] = Long  .toString(marketOrder.getTimestamp());
				result[3] = Double.toString(marketOrder.getUnits    ());

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class GetOrders extends Base {

		public GetOrders(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				Vector<?> orders = session
						.getClient()
						.getUser()
						.getAccountWithId(Integer.parseInt(commandArguments[0]))
						.getOrders();
				result = new String[orders.size()];

				for (int index = 0; index < orders.size(); ++index) {
					com.oanda.fxtrade.api.LimitOrder limitOrder = (com.oanda.fxtrade.api.LimitOrder) orders
							.get(index);
					String serializedOrders = limitOrder.getHighPriceLimit()
							+ "|" + limitOrder.getLowPriceLimit() + "|"
							+ limitOrder.getPair().getBase() + "|"
							+ limitOrder.getPair().getQuote() + "|"
							+ limitOrder.getPrice() + "|"
							+ limitOrder.getStopLoss() + "|"
							+ limitOrder.getTakeProfit() + "|"
							+ limitOrder.getTimestamp() + "|"
							+ limitOrder.getTransactionNumber() + "|"
							+ limitOrder.getExpiry() + "|"
							+ limitOrder.getUnits() + "|";

					result[index] = serializedOrders;
				}

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class GetTrades extends Base {

		public GetTrades(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;
			try {
				Vector<?> trades = session
						.getClient()
						.getUser()
						.getAccountWithId(Integer.parseInt(commandArguments[0]))
						.getTrades();

				result = new String[trades.size()];

				for (int index = 0; index < trades.size(); ++index) {
					MarketOrder marketOrder = (MarketOrder) trades.get(index);
					result[index] = serializeMarketOrder(marketOrder);
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class GetHistoryPoints extends Base {

		public GetHistoryPoints(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;
			try {
				FXPair pair = session.getApiFactory().createFXPair(
						commandArguments[0] + "/" + commandArguments[1]);
				Vector<?> historyPoints = null;

				historyPoints = session.getClient().getRateTable().getHistory(
						pair, new Long(commandArguments[2]),
						new Integer(commandArguments[3]));

				result = new String[historyPoints.size()];
				for (int index = 0; index < historyPoints.size(); ++index) {
					FXHistoryPoint historyPoint = (FXHistoryPoint) historyPoints
							.get(index);
					String serializedPoint = historyPoint.getTimestamp() + "|"
							+ historyPoint.getOpen().getBid() + "|"
							+ historyPoint.getOpen().getAsk() + "|"
							+ historyPoint.getClose().getBid() + "|"
							+ historyPoint.getClose().getAsk() + "|"
							+ historyPoint.getMax().getBid() + "|"
							+ historyPoint.getMin().getBid() + "|"
							+ historyPoint.getMax().getAsk() + "|"
							+ historyPoint.getMin().getAsk() + "|";
					result[index] = serializedPoint;
				}
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	abstract class Base implements ProtocolCommandExecutor {
		protected Session session;

		public Base(Session session) {
			this.session = session;
		}

		public abstract String[] execute(String[] commandArguments);
	}

	public class Login extends Base {
		public Login(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			Logger.getInstance().log(getClass().getName() + "::execute()");

			String[] result = null;

			try {

				if(commandArguments.length > 2) {
					session.getClient().login(commandArguments[0], commandArguments[1], commandArguments[2]);
				}
				else {
					session.getClient().login(commandArguments[0],
							commandArguments[1], "ProtocolCommands Login");
				}

			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class Logout extends Base {
		public Logout(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				session.cleanup();

				if (session.getClient().isLoggedIn()) {
					session.getClient().logout();
				}
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class IsLoggedIn extends Base {
		public IsLoggedIn(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			String[] result = new String[1];
			result[0] = "false";
			try {
				if (session.getClient().isLoggedIn()) {
					result[0] = "true";
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class SetWithRateThread extends Base {
		public SetWithRateThread(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				boolean isWithRateThread = true;
				if (commandArguments[0].equalsIgnoreCase("false")) {
					isWithRateThread = false;
				}
				session.getClient().setWithRateThread(isWithRateThread);
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class SetWithKeepAliveThread extends Base {
		public SetWithKeepAliveThread(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				boolean isWithKeepAliveThread = true;
				if (commandArguments[0].equalsIgnoreCase("false")) {
					isWithKeepAliveThread = false;
				}
				session.getClient().setWithKeepAliveThread(isWithKeepAliveThread);
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class SetTimeout extends Base {
		public SetTimeout(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);
			String[] result = null;

			try {
				Integer timeout = new Integer(commandArguments[0]);
				session.getClient().setTimeout(timeout);
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class SetRateTimeout extends Base {
		public SetRateTimeout(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			try {
				Integer timeout = new Integer(commandArguments[0]);
				session.getClient().setRateTimeout(timeout);
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class GetPairs extends Base {
		public GetPairs(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Collection<FXPair> pairs = null;
			String[] result = null;
			try {
				pairs = session.getClient().getRateTable().getAllSymbols();
				result = new String[pairs.size() * 2];

				int index = 0;
				for (FXPair pair : pairs) {
					result[index++] = pair.getBase();
					result[index++] = pair.getQuote();
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetRate extends Base {
		public GetRate(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			FXPair pair = session.getApiFactory().createFXPair(
					commandArguments[0] + "/" + commandArguments[1]);
			FXTick rate = null;
			String[] result = null;
			try {
				rate = session.getClient().getRateTable().getRate(pair);

				result = new String[4];
				if (rate != null) {
					result[0] = new Double(rate.getBid()).toString();
					result[1] = new Double(rate.getAsk()).toString();
					result[2] = new Long(rate.getTimestamp()).toString();
					result[3] = new Long(rate.getMaxUnits()).toString();
				}
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class GetRateForUnits extends Base {
	    public GetRateForUnits(Session session) {
	        super(session);
	    }

	    public String[] execute(String[] commandArguments) {
	        LogExecutorInvocation(getClass(), commandArguments);

	        FXPair pair = session.getApiFactory().createFXPair(
	                commandArguments[0] + "/" + commandArguments[1]);
	        FXTick rate = null;
	        String[] result = null;
	        try {
	            rate = session.getClient().getRateTable().getRateForUnits(pair, new Long(commandArguments[2]));

	            result = new String[4];
	            if (rate != null) {
	                result[0] = new Double(rate.getBid()).toString();
	                result[1] = new Double(rate.getAsk()).toString();
	                result[2] = new Long(rate.getTimestamp()).toString();
	                result[3] = new Long(rate.getMaxUnits()).toString();
	            } else {
	                throw new Exception("No rate for given units");
	            }
	        } catch (Exception e) {
	            result = processException(e);
	        }
	        return result;
	    }
	}

	public class GetRatesForAllUnits extends Base {
	    public GetRatesForAllUnits(Session session) {
	        super(session);
	    }

	    public String[] execute(String[] commandArguments) {
	        LogExecutorInvocation(getClass(), commandArguments);

	        FXPair pair = session.getApiFactory().createFXPair(
	                commandArguments[0] + "/" + commandArguments[1]);
	        Collection<FXTick> rates = null;
	        String[] result = null;
	        try {
	            rates = session.getClient().getRateTable().getRatesForAllUnits(pair);

                result = new String[rates.size() * 4];
                int index = 0;
                FXTick tick = null;
                for ( Iterator<FXTick> iter = rates.iterator(); iter.hasNext(); ) {
                    tick = iter.next();
                    result[index * 4] = new Double(tick.getBid()).toString();
                    result[index * 4 + 1] = new Double(tick.getAsk()).toString();
                    result[index * 4 + 2] = new Long(tick.getTimestamp()).toString();
                    result[index * 4 + 3] = new Long(tick.getMaxUnits()).toString();
                    ++index;
                }
	        } catch (Exception e) {
	            result = processException(e);
	        }
	        return result;
	    }
	}

	public class GetServerTime extends Base {
		public GetServerTime(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {

			String[] result = null;
			try {
				result = new String[1];
				result[0] = Long.toString(session.getClient().getServerTime());
			}
			catch(Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class SubscribeListener extends Base {
		public SubscribeListener(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String eventType = commandArguments[0];
			int accountId = Integer.parseInt(commandArguments[1]);

			String[] result = new String[1];
			UUID uid = UUID.randomUUID();

			try {
				if (eventType.equals("RATE_EVENT")) {
					RateEventListener rateEventListener = new RateEventListener(
							session.getEvents(), uid.toString());
					session.getClient().getRateTable().getEventManager().add(
							rateEventListener);
					session.mapListener(rateEventListener, uid.toString());
				}
				else if (eventType.equals("ACCOUNT_EVENT")) {
					AccountEventListener accountEventListener = new AccountEventListener(
							session.getEvents(), uid.toString());
					Account account = null;
					account = session.findAccount(accountId);

					account.getEventManager().add(accountEventListener);
					session.mapListener(accountEventListener, uid
							.toString());
				}
				else if (eventType.equals("SESSION_EVENT")) {
					session.addObserver(uid.toString());
				}
				result[0] = uid.toString();
			} catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class UnsubscribeListener extends Base {
		public UnsubscribeListener(Session session) {
			super(session);
		}

		@Override
		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;

			String listenerUid = commandArguments[0];
			FXEvent eventListener = session.findMappedListener(listenerUid);
			try {
				if (eventListener instanceof RateEventListener) {
					session.getClient().getRateTable().getEventManager()
							.remove(eventListener);
				} else {
					Vector<?> accounts = session.getClient().getUser()
							.getAccounts();
					for (int index = 0; index < accounts.size(); ++index) {
						((Account) accounts.get(index)).getEventManager()
								.remove(eventListener);
					}
				}

				session.unmapListener(listenerUid);
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}

	}

	public class GetEvents extends Base {
		private static final int MAX_EVENTS_COUNT = 1000;

		public GetEvents(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			Vector<String> events = new Vector<String>();
			// System.out.println(session.getEvents().size());

			String[] result = null;
			try {
				while (session.getEvents().hasMore()
						&& (events.size() < MAX_EVENTS_COUNT)) {
					Events.Record eventRecord = session.getEvents().getNext();
					events.add("EVENT_MARKER");
					events.addAll(eventRecord.toStringArray());
				}

				result = new String[events.size()];
				events.toArray(result);
			}
			catch (Exception e) {
				result = processException(e);
			}
			return result;
		}
	}

	public class GetPositions extends Base {
		public GetPositions(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Vector<?> positions = null;

			String[] result = null;
			try {
				positions = session.getClient().getUser().getAccountWithId(
						Integer.parseInt(commandArguments[0])).getPositions();
				result = new String[positions.size()];

				for (int index = 0; index < positions.size(); ++index) {
					Position position = (Position) positions.get(index);
					String serializedTrade = position.getPair().getPair() + "|"
							+ position.getUnits() + "|" + position.getPrice()
							+ "|";
					result[index] = serializedTrade;
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetInstrument extends Base {
		public GetInstrument(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;
			try {
				FXPair pair = session.getApiFactory().createFXPair(
						commandArguments[0] + "/" + commandArguments[1]);
				Instrument instrument = session.getClient().getRateTable().getInstrument(
						pair.getPair());

				result = new String[1];

				String serializedInstrument = instrument.getCurrentTick()
						.getTimestamp()
						+ "|"
						+ instrument.getSymbol()
						+ "|"
						+ instrument.getMaxMargin()
						+ "|"
						+ instrument.getPrecision()
						+ "|"
						+ instrument.getPipettes()
						+ "|"
						+ instrument.getCategory()
						+ "|"
						+ instrument.getStatus() + "|";
				result[0] = serializedInstrument;
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetCandles extends Base {
		public GetCandles(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Vector<?> candles = null;

			String[] result = null;
			try {
				FXPair pair = session.getApiFactory().createFXPair(
						commandArguments[0] + "/" + commandArguments[1]);
				candles = session.getClient().getRateTable().getCandles(pair,
						new Long(commandArguments[2]),
						new Integer(commandArguments[3]));

				result = new String[candles.size()];

				for (int index = 0; index < candles.size(); ++index) {
					CandlePoint candle = (CandlePoint) candles.get(index);
					String serializedCandle = candle.getTimestamp() + "|"
							+ candle.getOpen() + "|" + candle.getClose() + "|"
							+ candle.getMin() + "|" + candle.getMax() + "|";
					result[index] = serializedCandle;
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class GetMinMaxs extends Base {
		public GetMinMaxs(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			Vector<?> minMaxs = null;

			String[] result = null;
			try {
				FXPair pair = session.getApiFactory().createFXPair(
						commandArguments[0] + "/" + commandArguments[1]);
				minMaxs = session.getClient().getRateTable().getMinMaxs(
						pair,
						new Long(commandArguments[2]),
						new Integer(commandArguments[3]));

				result = new String[minMaxs.size()];

				for (int index = 0; index < minMaxs.size(); ++index) {
					MinMaxPoint minMax = (MinMaxPoint) minMaxs.get(index);
					String serializedMinMax = minMax.getTimestamp() + "|"
							+ minMax.getMin() + "|" + minMax.getMax() + "|";
					result[index] = serializedMinMax;
				}
			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class SetLogfile extends Base {
		public SetLogfile(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String logfile = commandArguments[0];
			Logger.getInstance().setLogfile(logfile);
			return null;
		}
	}

	public class GetSessionKey extends Base {
		public GetSessionKey(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			LogExecutorInvocation(getClass(), commandArguments);

			String[] result = null;
			try {
				String sessionKey = session.fxClient.getSessionKey();
				result = new String[1];
				result[0] = sessionKey;

			} catch (Exception e) {
				result = processException(e);
			}

			return result;
		}
	}

	public class UnsupportedCommand extends Base {
		public UnsupportedCommand(Session session) {
			super(session);
		}

		public String[] execute(String[] commandArguments) {
			String[] result = null;

			result = processException(new Exception("CommandIsNotSupported"));
			return result;
		}
	}

	public String[] processException(Exception e) {
		String[] result = new String[2];
		result[0] = "error";
		result[1] = e.toString();
		Logger.getInstance().log("exception!");
		Logger.getInstance().log(e.toString());
		return result;
	}

	public String serializeMarketOrder(MarketOrder marketOrder) {
		String serializedMarketOrder =
			marketOrder.getHighPriceLimit() + "|"
			+ marketOrder.getLowPriceLimit() + "|"
			+ marketOrder.getPair().getBase() + "|"
			+ marketOrder.getPair().getQuote() + "|"
			+ marketOrder.getPrice() + "|"
			+ marketOrder.getStopLoss()	+ "|"
			+ marketOrder.getTakeProfit() + "|"
			+ marketOrder.getTimestamp() + "|"
			+ marketOrder.getTransactionNumber() + "|"
			+ marketOrder.getUnits() + "|"
			+ marketOrder.getRealizedPL()	+ "|"
			+ marketOrder.getTransactionLink() + "|"
			+ marketOrder.getTrailingStopLoss() + "|";

		return serializedMarketOrder;
	}

	public void LogExecutorInvocation(Class<?> clazz,
			String[] commandArguments) {
		String params = clazz.getName() + "::execute()";

		if(commandArguments == null) {
			return;
		}

		if(commandArguments.length > 0) {
			params += " [" + commandArguments[0] + "]";
		}
		if(commandArguments.length > 1) {
			params += ", [" + commandArguments[1] + "]";
		}
		Logger.getInstance().log(params);
	}

}
