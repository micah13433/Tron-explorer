package com.tron.explorer.cron;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.model.Nodes;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.NodeService;

public class FetchNodeJob implements Job{
	
	private Logger log = LogManager.getLogger(FetchNodeJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Nodes nodes;
		try {
			nodes = NodeService.queryNodes();
			if(nodes == null || nodes.getTotalCount() <= 0){
				return;
			}
			CacheKit.remove("nodeList", "index");
			CacheKit.put("nodeList", "index", nodes);
			CacheKit.put("persistedList", "nodeNum", nodes.getTotalCount());
		} catch (TronException ex) {
			log.error("节点初始化=> [失败]:" + ex.getLocalizedMessage());
		}
	}

}
