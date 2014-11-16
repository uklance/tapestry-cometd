package org.lazan.t5.cometddemo.model;

public class StockPrice {
	private String ticker;
	private int increment;
	private double price;
	private boolean increase;
	public StockPrice(String ticker, int increment, double price, boolean increase) {
		super();
		this.ticker = ticker;
		this.increment = increment;
		this.price = price;
		this.increase = increase;
	}
	public String getTicker() {
		return ticker;
	}
	public int getIncrement() {
		return increment;
	}
	public double getPrice() {
		return price;
	}
	public boolean isIncrease() {
		return increase;
	}
}
