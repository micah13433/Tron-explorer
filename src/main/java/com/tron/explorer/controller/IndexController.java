package com.tron.explorer.controller;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.Block;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.Transactions;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AccountService;
import com.tron.explorer.service.BlockService;
import com.tron.explorer.util.PageUtil;
import com.tron.explorer.util.StringUtils;
import com.tron.explorer.util.XssUtil;

public class IndexController extends Controller {

	public void index() throws TronException {
		long maxBlockHeight = BlockService.getLatestBlockNumber();
		List<Block> blocks = BlockService.queryLatestBlocks();
		int transCount = 0;
		List<Transaction> transactionList = CacheKit.get("transactionList",
				"index");
		if (transactionList != null) {
			transactionList = transactionList.size() > 10 ? transactionList
					.subList(0, 10) : transactionList;
			transCount = transactionList.size();
		}		
		setAttr("blocks", blocks);
		setAttr("maxBlockHeight", maxBlockHeight);		
		setAttr("transactions", transactionList);
		setAttr("transCount", transCount);
		render("index.html");
	}

	public void search() throws TronException {
		String q = getPara("q");
		if (StringUtils.isEmpty(q)) {
			index();
			return;
		}
		if (StringUtils.isNumeric(q)) {
			long height = Long.valueOf(q);
			Block block = BlockService.getBlockByHeight(height, true);
			setAttr("block", block);
			setAttr("transactions", block.getTradeList());
			render("block/detail.html");
		} else {
			q = XssUtil.xssEncode(q);
			Account account = AccountService.getAccountByAddress(q);
			Long currPageIndex = PageUtil.getPage(getPara("page"));	
			BaseQuery baseQuery = new BaseQuery();
			baseQuery.setLimit(Constrain.pageSize);
			baseQuery.setOffset((currPageIndex-1)*Constrain.pageSize);
			baseQuery.setSort("-timestamp");
			Transactions transactions = AccountService.queryTransactionList(q,baseQuery);
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
			setAttr("assertMap", account.getAssets());
			setAttr("assetCount", account.getAssets() == null ? 0 : account
					.getAssets().size());
			render("account/detail.html");
		}

	}
}
