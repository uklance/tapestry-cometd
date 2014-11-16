package org.lazan.t5.cometddemo.pages;

import javax.inject.Inject;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.lazan.t5.cometddemo.model.StockPrice;
import org.lazan.t5.cometddemo.services.StockService;

@Import(library={
	"classpath:/org/lazan/t5/cometddemo/jquery.flot.js",
	"classpath:/org/lazan/t5/cometddemo/pages/Stocks.js"
})
public class Stocks {
	@Inject
	private Block stockPriceBlock1;
	
	@Inject
	private StockService stockService;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Property
	private StockPrice stockPrice;
	
	@Property
	private String ticker;
	
	public String[] getTickers() {
		return stockService.getTickers();
	}
	
	public String getTopic() {
		return stockService.getTopic(ticker);
	}
	
	public String getStockColor() {
		return stockPrice.isIncrease() ? "green" : "red";
	}
	
	public Block onStockPriceReceived1(StockPrice stockPrice) {
		this.stockPrice = stockPrice;
		return stockPriceBlock1;
	}

	public void onStockPriceReceived2(final StockPrice stockPrice) {
		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			public void run(JavaScriptSupport jss) {
				jss.addScript("window.addDataPoint('%s', %s, %s);", stockPrice.getTicker(), stockPrice.getIncrement(), stockPrice.getPrice());
			}
		});
	}
}
