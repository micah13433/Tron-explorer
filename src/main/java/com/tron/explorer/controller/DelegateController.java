package com.tron.explorer.controller;


import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.Delegate;
import com.tron.explorer.model.Delegates;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AccountService;
import com.tron.explorer.util.PageUtil;

public class DelegateController extends Controller {
		
	public void index() throws TronException {
		Long currPageIndex = PageUtil.getPage(getPara("page"));		
		Delegates delegates = CacheKit.get("delegateList", "index");
		if(delegates == null){
			setAttr("count",0);
			render("delegate/index.html");
			return;
		}
		int [] pageIndexArray = PageUtil.getPageIndex(delegates.getDelegates(),currPageIndex);
		List<Delegate> delecateList = delegates.getDelegates().subList(pageIndexArray[0], pageIndexArray[1]);		
	
		setAttr("currpage",currPageIndex);
		setAttr("totalpage",delegates.getDelegates().size()/Constrain.pageSize + 1);
		setAttr("delegates", delecateList);
		setAttr("count", delegates.getDelegates().size());
		render("delegate/index.html");
	}
	
	public void list() throws TronException{
		Delegates delegates = CacheKit.get("delegateList", "index");
		if(delegates == null){
			delegates = new Delegates();
		}
		setAttr("delegates", delegates.getDelegates());
		Account account = AccountService.getAccountByAddress((String)this.getSession().getAttribute("address"));
		if(account != null){
			setAttr("frozenAmount", account.getFrozenAmount());
		}
		renderJson();
	}
	
	public void count() throws TronException{
		renderText(String.valueOf(CacheKit.get("persistedList", "delegateNum")));
	}

}
 