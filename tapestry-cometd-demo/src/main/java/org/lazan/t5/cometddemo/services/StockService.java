package org.lazan.t5.cometddemo.services;

public interface StockService {

	String[] getTickers();

	String getTopic(String ticker);

}