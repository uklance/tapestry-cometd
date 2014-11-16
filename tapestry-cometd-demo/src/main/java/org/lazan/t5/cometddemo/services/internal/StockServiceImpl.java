package org.lazan.t5.cometddemo.services.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.lazan.t5.cometd.services.PushManager;
import org.lazan.t5.cometddemo.model.StockPrice;
import org.lazan.t5.cometddemo.services.StockService;

public class StockServiceImpl implements StockService {
	private static final String[] TICKERS = { "GOOG", "YAHOO", "IBM", "SONY" };
	private PushManager pushManager;

	public StockServiceImpl(PushManager pushManager, ParallelExecutor executor) {
		super();
		this.pushManager = pushManager;
		executor.invoke(new StockWorker());
	}

	@Override
	public String[] getTickers() {
		return TICKERS;
	}
	
	@Override
	public String getTopic(String ticker) {
		return "/stocks/" + ticker;
	}

	private class StockWorker implements Invokable<Void> {
		public Void invoke() {
			Random random = new Random();
			Map<String, StockPrice> prevPrices = new HashMap<String, StockPrice>();
			int maxPrice = 100;
			while (true) {
				for (String ticker : TICKERS) {
					StockPrice prevPrice = prevPrices.get(ticker);
					double price = random.nextInt(maxPrice * 100) / 100D;
					boolean increase = (prevPrice == null) ? true : (price > prevPrice.getPrice());
					int increment = (prevPrice == null) ? 1 : prevPrice.getIncrement() + 1;
					String topic = getTopic(ticker);
					StockPrice newPrice = new StockPrice(ticker, increment, price, increase);
					pushManager.broadcast(topic, newPrice);
					prevPrices.put(ticker, newPrice);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
