package com.tron.explorer.controller;


import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.Accounts;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.Transactions;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AccountService;
import com.tron.explorer.util.PageUtil;

public class AccountController extends Controller {
		
	public void index() throws TronException {
		Long currPageIndex = PageUtil.getPage(getPara("page"));	
		BaseQuery baseQuery = new BaseQuery();
		baseQuery.setLimit(Constrain.pageSize);
		baseQuery.setOffset((currPageIndex-1)*Constrain.pageSize);
		baseQuery.setSort("-balance");
		Accounts accounts = AccountService.queryAccounts(baseQuery);
		setAttr("currpage",currPageIndex);
		setAttr("totalpage",accounts.getTotalCount()/Constrain.pageSize + 1);
		setAttr("accounts", accounts.getAccounts());
		setAttr("count", accounts.getTotalCount());
		render("account/index.html");
	}
	
	public void voteList() throws TronException {
		String address = getPara("address");
		Account account = AccountService.getAccountByAddress(address);
		setAttr("voteList", account.getVotes());
		setAttr("address", account.getAddress());
		render("account/voteList.html");
	}
	
	public void assertList() throws TronException {
		String address = getPara("address");
		Account account = AccountService.getAccountByAddress(address);
		setAttr("assertMap", account.getAssets());
		setAttr("count", account.getAssets() == null ? 0:  account.getAssets().size());
		setAttr("address", account.getAddress());
		render("account/assertList.html");
	}
	
	public void count() throws TronException{
		renderText(String.valueOf(CacheKit.get("persistedList", "accountNum")));
	}
	
	public void detail() throws TronException {
		String address = getPara("address");
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
		setAttr("assertMap",account.getAssets());
		setAttr("assetCount", account.getAssets() == null ? 0:  account.getAssets().size());
		render("account/detail.html");
	}
}
 