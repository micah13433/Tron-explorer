package com.tron.explorer.controller;


import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.TronException;

public class MarketController extends Controller {
	
	public void index() {
		render("market/index.html");
	}
	
	public void priceList() throws TronException {
		renderJson("markets",CacheKit.get("chartList", "second"));
	}
	
	public void chartData() throws TronException {	
		renderJson("data",CacheKit.get("chartList", "index"));
	}
}
 