package com.tron.explorer.controller;


import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.PageUtil;

public class TransController extends Controller {
		
	public void index() throws TronException {		
		Long currPageIndex = PageUtil.getPage(getPara("page"));		
		List<Transaction> listInMem = CacheKit.get("transactionList", "index");	
		if(listInMem == null){
			listInMem = new ArrayList<Transaction>();
		}else{
			setAttr("totalpage",listInMem.size()/Constrain.pageSize + 1);
			int [] pageIndexArray = PageUtil.getPageIndex(listInMem,currPageIndex);
			listInMem = listInMem.subList(pageIndexArray[0], pageIndexArray[1]);		
		}
		setAttr("currpage",currPageIndex);
		setAttr("count", listInMem.size());
		setAttr("transactions",listInMem);
		render("transaction/index.html");
	}

}
 