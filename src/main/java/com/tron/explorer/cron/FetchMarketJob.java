package com.tron.explorer.cron;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.Market;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.MarketService;
import com.tron.explorer.util.StringUtils;

public class FetchMarketJob implements Job{
	
	private Logger log = LogManager.getLogger(FetchMarketJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String chartJsonData;
		try {
			chartJsonData = MarketService.queryChartData();
			if(StringUtils.isBlank(chartJsonData)){
				return;
			}
			JSONObject obj=JSON.parseObject(chartJsonData);
			if(obj == null || obj.get("code") == null || obj.getInteger("code") != 0){
				return;
			}
			CacheKit.remove("chartList", "index");
			CacheKit.put("chartList", "index", obj);
			List<Market> list = MarketService.queryPrices();
			if(list == null || list.size() <= 0 ){
				return;
			}
			CacheKit.remove("chartList", "second");
			CacheKit.put("chartList", "second", list);
		} catch (TronException ex) {
			log.error("市场初始化=> [失败]:" + ex.getLocalizedMessage());
		}
	}

}
