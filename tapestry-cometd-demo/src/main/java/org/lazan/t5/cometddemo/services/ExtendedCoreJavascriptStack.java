package org.lazan.t5.cometddemo.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.services.javascript.CoreJavaScriptStack;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.AssetSource;

public class ExtendedCoreJavascriptStack extends CoreJavaScriptStack {
	private Asset jquery;
	
	public ExtendedCoreJavascriptStack(
			@Symbol(SymbolConstants.PRODUCTION_MODE) boolean productionMode, 
			SymbolSource symbolSource,
			AssetSource assetSource, 
			ThreadLocale threadLocale,
			@Path("context:js/jquery-1.9.1.js") Asset jquery) 
	{
		super(productionMode, symbolSource, assetSource, threadLocale);
		this.jquery = jquery;
	}
	
	@Override
	public List<Asset> getJavaScriptLibraries() {
		List<Asset> libraries = new ArrayList<Asset>(super.getJavaScriptLibraries());
		libraries.add(jquery);
		return libraries;
	}
}