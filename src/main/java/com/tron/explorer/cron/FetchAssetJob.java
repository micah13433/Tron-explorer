package com.tron.explorer.cron;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.Assets;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AssetService;

public class FetchAssetJob implements Job{
	
	private Logger log = LogManager.getLogger(FetchAssetJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Assets assets;
		try {
			assets = AssetService.queryAsserts();
			if(assets == null || assets.getCount() < 0){
				return;
			}
			CacheKit.remove("assetList", "index");
			CacheKit.put("assetList", "index", assets);
			CacheKit.put("persistedList", "assetNum", assets.getCount());
		} catch (TronException ex) {
			log.error("资产初始化=> [失败]:" + ex.getLocalizedMessage());
		}
	}

}
