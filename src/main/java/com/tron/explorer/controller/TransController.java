package com.tron.explorer.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Block;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.BlockService;
import com.tron.explorer.service.TransService;
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
	
	public void detail() throws TronException {
		String hash = getPara("hash");
		if(StringUtils.isBlank(hash) || hash.length() < 64){
			setAttr("status", 0);
		}else{
			Transaction trans = TransService.getTransactionByHash(hash);
			setAttr("status", 1);
			setAttr("votes", trans.getVotes());
			setAttr("transaction",trans);

		}		
		render("transaction/detail.html");
	}
	
	public void sync() throws TronException {
		long from = getParaToLong("from");
		long to = getParaToLong("to");
		for(long i= from; i< to;i++){
			Block block = BlockService.getBlockByHeight(i);
			if(block == null || block.getHeight() == 0) return;		
			List<Transaction> transactions = CacheKit.get("transactionList", "index");
			if(transactions == null){
				transactions = new ArrayList<Transaction>();					
			}
	
			block.getTradeList().addAll(transactions);
			CacheKit.put("transactionList", "index", block.getTradeList());
		}
		
	}
}
 