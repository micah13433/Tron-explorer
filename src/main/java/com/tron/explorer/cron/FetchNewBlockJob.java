package com.tron.explorer.cron;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.Block;
import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.BlockService;

public class FetchNewBlockJob implements Job{
	
	private Logger log = LogManager.getLogger(FetchNewBlockJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			Long currBlockHeightInCache = CacheKit.get("persistedList", "maxBlockHeight");
			boolean updateBlockNum = false;
			if(currBlockHeightInCache == null){
				currBlockHeightInCache = BlockService.getLatestBlockNumber();
				updateBlockNum = false;
			}
			if (currBlockHeightInCache != 0) {
				if(updateBlockNum){
					currBlockHeightInCache++;
				}
				Block block = BlockService.getBlockByHeight(currBlockHeightInCache);
				if(block == null || block.getHeight() == 0) return;
				CacheKit.put("persistedList", "maxBlockHeight", currBlockHeightInCache + 1);
				
				List<Transaction> transactions = CacheKit.get("transactionList", "index");
				if(transactions == null){
					transactions = new ArrayList<Transaction>();					
				}

				block.getTradeList().addAll(transactions);
				CacheKit.remove("transactionList", "index");
				CacheKit.put("transactionList", "index", block.getTradeList());
			}
		     
		} catch (TronException ex) {	
			log.error("区块初始化=> [失败]:" + ex.getLocalizedMessage());
		}
		
	}

}
