package com.tron.explorer.cron;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.Delegates;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.DelegateService;

public class FetchDelegateJob implements Job {
	
private Logger log = LogManager.getLogger(FetchDelegateJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Delegates delegates;
		try {
			delegates = DelegateService.queryDelegates(true);
			if(delegates == null || delegates.getTotalCount() <= 0){
				return;
			}
			CacheKit.remove("delegateList", "index");
			CacheKit.put("delegateList", "index", delegates);
			CacheKit.put("persistedList", "delegateNum", delegates.getTotalCount());
		} catch (TronException ex) {
			log.error("受托人初始化=> [失败]:" + ex.getLocalizedMessage());
		}
	}
}
