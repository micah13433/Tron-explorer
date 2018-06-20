package com.tron.explorer.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.helper.StringUtil;
import org.tron.common.crypto.ECKey;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.interceptor.AuthInterceptor;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.Delegates;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.Transactions;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AccountService;
import com.tron.explorer.service.MarketService;
import com.tron.explorer.service.NewsService;
import com.tron.explorer.service.WalletService;
import com.tron.explorer.util.CheckUtil;
import com.tron.explorer.util.DecodeUtil;
import com.tron.explorer.util.PageUtil;
import com.tron.explorer.util.StringUtils;
import com.tron.explorer.util.Utils;

public class WalletController extends Controller {
	
	@Before(AuthInterceptor.class)
	public void index() throws TronException {
		String address = (String) this.getSession().getAttribute("address");
		Account account = AccountService.getAccountByAddress(address);
		Long currPageIndex = PageUtil.getPage(getPara("page"));	
		BaseQuery baseQuery = new BaseQuery();
		baseQuery.setLimit(Constrain.pageSize);
		baseQuery.setOffset((currPageIndex-1)*Constrain.pageSize);
		baseQuery.setSort("-timestamp");
		Transactions transactions = AccountService.queryTransactionList(address,baseQuery);
		if(transactions == null || transactions.getCount() <= 0){
			setAttr("transcount", 0);
		}else{			
			List<Transaction> innerList = transactions.getOrders();		
			setAttr("transcount", transactions.getCount());
			setAttr("tranactions", innerList);
			setAttr("currpage",currPageIndex);
			setAttr("totalpage",transactions.getCount()/Constrain.pageSize + 1);
		}
		setAttr("account", account);
		setAttr("voteList", account.getVotes());
		setAttr("frozenList", account.getFrozens());
		Map<String, Object> assetMap = new HashMap<String, Object>();
		//assetMap.put("TRX", account.getBalance());
		JSONArray assetArray = new JSONArray();
		JSONObject obj = new JSONObject();
		 obj.put("name", "TRX 【" + account.getBalance() + "】");
	     obj.put("value", "TRX");
	     assetArray.add(obj);
		if(account.getAssets() != null){
			Iterator<Entry<String, Long>> it = account.getAssets().entrySet().iterator(); 
			   while(it.hasNext()){  
			     Entry<String, Long> entry=it.next();  
			     assetMap.put(entry.getKey(), String.valueOf(entry.getValue()));
			     obj = new JSONObject();
			     obj.put("name", entry.getKey() + " 【" + entry.getValue() + "】");
			     obj.put("value", entry.getKey());
			     assetArray.add(obj);
			 }  
		}
		setAttr("assertMap",assetMap);		
		setAttr("assertJson",assetArray.toString());
		render("wallet/index.html");
	}
	
	public void login() throws TronException {
		
		render("wallet/login.html");
	}
	
	public void logout() throws TronException {
		if(this.getSession().getAttribute("address") != null){
			this.getSession().removeAttribute("address");
			this.getSession().removeAttribute("password");
		}
		renderJson();
	}
	
	public void doLogin() throws TronException {
		String password = getPara("password");
		String address = getPara("address");
		String from = getPara("from");
		JSONObject obj = new JSONObject();
		if(StringUtils.isBlank(password) || password.length() < 40){
			obj.put("status", 0);
		}
		Account account = WalletService.getAccountByPassword(password);
		if(account == null){
			obj.put("status", 0);
		}else{
			obj.put("status", 1);
			if(StringUtil.isBlank(address)){
				address = account.getAddress();
			}
			if(!StringUtils.isBlank(from)){
				obj.put("TRX", account.getBalance());
				Iterator<Entry<String, Long>> entries = account.getAssets().entrySet().iterator();  				  
				while (entries.hasNext()) {  				  
				    Entry<String, Long> entry = entries.next(); 
				    obj.put(entry.getKey(), entry.getValue());
				}  
				obj.put("price", WalletService.getCurrPrice());
				obj.put("address", address);
			}
			this.getSession().setAttribute("address", address);
			this.getSession().setAttribute("password", password);
		}
		obj.put("password", password);
		renderJson(obj);
	}
	
	public void generate() throws TronException {
		ECKey ecKey = new ECKey(Utils.getRandom());
		JSONObject obj = new JSONObject();
		obj.put("address", Base58.encodeChecked(ecKey.getAddress()));
		obj.put("password", DecodeUtil.bytesToHex(ecKey.getPrivKeyBytes()));
		renderJson(obj);
	}
	
	@Before(AuthInterceptor.class)
	public void send() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		String toAddress = getPara("toAddress");
		String amount = getPara("amount");
		String asset = getPara("asset");
		long amountToLong = (long) (Double.valueOf(amount) * 1000000);
		if(!asset.equals("TRX")){
			amountToLong = Long.valueOf(amount);			
		}
		if(!CheckUtil.isValidAddress(fromAddress) && !CheckUtil.isValidAddress(toAddress)){
			renderJson(obj);
			return;
		}
		boolean result = WalletService.send(asset,fromAddress, toAddress, amountToLong,password);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);
	}
	
	@Before(AuthInterceptor.class)
	public void getFreeTRX() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		String fromAddress = (String) this.getSession().getAttribute("address");
		boolean result = WalletService.getFreeTRX(fromAddress);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);
	}
	
	@Before(AuthInterceptor.class)
	public void vote() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		String addressList = getPara("addressList");
		String amountList = getPara("amountList");
		if(StringUtil.isBlank(addressList) || StringUtil.isBlank(amountList)){
			renderJson(obj);
			return;
		}
		HashMap<String, String> witness = new HashMap<String, String>();
		for(int i=0,length=addressList.split(",").length; i<length; i++){
			witness.put(addressList.split(",")[i], amountList.split(",")[i]);
		}
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		boolean result = WalletService.vote(fromAddress,password,witness);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);
	}
	
	@Before(AuthInterceptor.class)
	public void assetBuy() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		String assetname = getPara("assetname");
		String toaddress = getPara("toaddress");
		String amountpara = getPara("amount");
		long amount = (long) ((Float.valueOf(amountpara) * Constrain.ONE_TRX));
		if(StringUtil.isBlank(toaddress) || !CheckUtil.isValidAddress(toaddress)){
			renderJson(obj);
			return;
		}
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		boolean result = WalletService.assetBuy(assetname,fromAddress,toaddress,amount,password);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);

	}
	
	@Before(AuthInterceptor.class)
	public void freeze() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		
		String frozenBalance = getPara("frozenBalance");
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		long frozenDuration = 3L;
		long frozen_balance = (long) ((Double.valueOf(frozenBalance) * Constrain.ONE_TRX));
		boolean result = WalletService.freeze(fromAddress,frozen_balance,frozenDuration,password);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);		
	}
	
	@Before(AuthInterceptor.class)
	public void unfreeze() throws TronException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);		
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		boolean result = WalletService.unfreeze(fromAddress,password);
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);		
	}
	
	@Before(AuthInterceptor.class)
	public void assetIssue() throws TronException, ParseException {
		JSONObject obj = new JSONObject();
		obj.put("code", 0);		
		String fromAddress = (String) this.getSession().getAttribute("address");
		String password = (String) this.getSession().getAttribute("password");
		String assetname = getPara("name");
		long supply = getParaToLong("supply");
		String price = getPara("price");
		String url = getPara("url");
		String desc = getPara("desc");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		Date startDate = sdf.parse(startTime);   
		Date endDate = sdf.parse(endTime); 
		int assetNum = 1;
		int trxNum = (int) (Float.parseFloat(price) * Constrain.ONE_TRX);
		boolean result = WalletService.assetIssue(fromAddress,password,assetname,supply,assetNum,trxNum,url,desc,startDate.getTime(),endDate.getTime());
		if(result){
			obj.put("code", 1);
		}
		renderJson(obj);		
	}
	
	public void getLatestAsset() throws TronException{
		JSONObject obj = new JSONObject();
		String password = (String) this.getSession().getAttribute("password");
		if(password == null){
			obj.put("code", 0);
		}else{
			obj.put("code", 1);
			String fromAddress = (String) this.getSession().getAttribute("address");
			Account account = WalletService.getAccountByPassword(password);
			obj.put("TRX", account.getBalance());
			Iterator<Entry<String, Long>> entries = account.getAssets().entrySet().iterator();  				  
			while (entries.hasNext()) {  				  
			    Entry<String, Long> entry = entries.next(); 
			    obj.put(entry.getKey(), entry.getValue());
			}  
			obj.put("price", WalletService.getCurrPrice());
			obj.put("address", fromAddress);
		}
		
		renderJson(obj);		
	}
	
	public void getTransactionHistory() throws TronException{
		JSONObject obj = new JSONObject();
		String password = (String) this.getSession().getAttribute("password");
		if(password == null){
			obj.put("code", 0);
		}else{
			obj.put("code", 1);
			Long currPageIndex = PageUtil.getPage(getPara("page"));	
			BaseQuery baseQuery = new BaseQuery();
			int pageSize = 10;
			baseQuery.setLimit(pageSize);
			baseQuery.setOffset((currPageIndex-1)*pageSize);
			baseQuery.setSort("-timestamp");
			String address = (String) this.getSession().getAttribute("address");
			Transactions transactions = AccountService.queryTransactionList(address,baseQuery);			
			obj.put("list", JSON.toJSONString(transactions.getOrders()));
			obj.put("address", address);
		}
		
		renderJson(obj);		
	}
	
	public void getNews() throws TronException{
		JSONObject obj = new JSONObject();
		obj.put("list", JSON.toJSONString(NewsService.queryNews()));
		renderJson(obj);		
	}
	
	public void getMarket() throws TronException{
		JSONObject obj = new JSONObject();
		obj.put("list", JSON.toJSONString(MarketService.queryPrice4Extension()));
		renderJson(obj);		
	}
	
	public void getWitness() throws TronException{
		JSONObject obj = new JSONObject();
		Delegates delegates = CacheKit.get("delegateList", "index");
		if(delegates == null){
			delegates = new Delegates();
		}
		obj.put("list", JSON.toJSONString(delegates.getDelegates()));
		renderJson(obj);		
	}
	
	public void checkUpdate() throws TronException{
		String localVersion = getPara("version");
		String latestVersion = Constrain.LASTEST_VERSION;
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		if(!Utils.isNewVersion(localVersion, latestVersion)){
			obj.put("code", 1);
		}
		renderJson(obj);		
	}
}
 