package com.tron.explorer.cron;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Accounts;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AccountService;

public class FetchAccountJob implements Job{
	
	private Logger log = LogManager.getLogger(FetchAccountJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		Accounts accounts;
		try {
			BaseQuery baseQuery = new BaseQuery();
			baseQuery.setLimit(0);
			baseQuery.setOffset(0);
			baseQuery.setSort("-balance");
			AccountService.queryAccounts(baseQuery);
//			if(accounts == null || accounts.getTotalCount() <= 0){
//				return;
//			}
//			CacheKit.remove("accountList", "index");
//			CacheKit.put("accountList", "index", accounts);
//			CacheKit.put("persistedList", "accountNum", accounts.getTotalCount());
		} catch (TronException ex) {
			log.error("用户初始化=> [失败]:" + ex.getLocalizedMessage());
		}
	}

}
