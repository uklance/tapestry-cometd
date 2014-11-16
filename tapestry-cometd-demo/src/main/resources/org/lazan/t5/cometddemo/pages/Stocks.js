jQuery(document).ready(function(){
	var dataByTicker = {};
	var maxDataPoints = 20;

	window.addDataPoint = function(ticker, increment, value) {
	    var tickerData = dataByTicker[ticker];
	    if (tickerData == null) {
	        tickerData = [];
	        dataByTicker[ticker] = tickerData;
	    }
	    var dataPoint = [increment, value];
	    tickerData.push(dataPoint);
	    if (tickerData.length > maxDataPoints) {
	    	tickerData.splice(0, 1);
	    }
	    
	    var allSeries = [];
	    for (var key in dataByTicker) {
	        var seriesEntry = {
	            label: key,
	            data: dataByTicker[key]
	        };
	        allSeries.push(seriesEntry);
	    }
	    jQuery.plot(jQuery("#chart"), allSeries, {});
	};
});