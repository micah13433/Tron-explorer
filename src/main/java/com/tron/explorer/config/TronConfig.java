package com.tron.explorer.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.tron.explorer.controller.AccountController;
import com.tron.explorer.controller.AssetController;
import com.tron.explorer.controller.BlockController;
import com.tron.explorer.controller.DelegateController;
import com.tron.explorer.controller.IndexController;
import com.tron.explorer.controller.MarketController;
import com.tron.explorer.controller.NodeController;
import com.tron.explorer.controller.TransController;
import com.tron.explorer.controller.WalletController;
import com.tron.explorer.cron.FetchAccountJob;
import com.tron.explorer.cron.FetchAssetJob;
import com.tron.explorer.cron.FetchDelegateJob;
import com.tron.explorer.cron.FetchMarketJob;
import com.tron.explorer.cron.FetchNewBlockJob;
import com.tron.explorer.cron.FetchNodeJob;
import com.tron.explorer.cron.QuartzManager;


public class TronConfig extends JFinalConfig {
	
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/", 5);
	}
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setDevMode(false);
		me.setError500View("/common/error.html");
		me.setError404View("/common/error.html");
		me.setErrorView(400, "/common/error.html");
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/");
		me.add("/block", BlockController.class, "/");
		me.add("/market", MarketController.class, "/");
		me.add("/delegate", DelegateController.class, "/");
		me.add("/account", AccountController.class, "/");
		me.add("/asset", AssetController.class, "/");
		me.add("/node", NodeController.class, "/");
		me.add("/transaction", TransController.class, "/");
		me.add("/wallet", WalletController.class, "/");
		
	}
	
	public void configEngine(Engine me) {
		me.addSharedFunction("/common/head.html");
		me.addSharedFunction("/common/foot.html");
		me.addSharedFunction("/common/page.html");
		me.addSharedFunction("/common/empty.html");
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		String path = this.getClass().getClassLoader().getResource("/").getPath();
		me.add(new EhCachePlugin(path + "encache.xml")); 

	}
	
	public void afterJFinalStart(){
		QuartzManager qm = new QuartzManager();
		String group = "IntervalJobGroup";
		String group2 = "OnceJobGroup";
		String name = "FetchAccountJob";
		String cronExpression = "0 0/1 * * * ?";
//		qm.addJob(name, group, FetchAccountJob.class, cronExpression);
		qm.addOnceJob(name, group2, FetchAccountJob.class);
		name = "FetchNewBlockJob";
		cronExpression = "0/3 * * * * ?";
		qm.addJob(name, group, FetchNewBlockJob.class, cronExpression);
		name = "FetchDelegateJob";
		cronExpression = "0 0/1 * * * ?";
		qm.addJob(name, group, FetchDelegateJob.class, cronExpression);
		qm.addOnceJob(name, group2, FetchDelegateJob.class);
		name = "FetchAssetJob";
		cronExpression = "0 0/5 * * * ?";
		qm.addJob(name, group, FetchAssetJob.class, cronExpression);
		qm.addOnceJob(name, group2, FetchAssetJob.class);
		name = "FetchNodeJob";
		cronExpression = "0 0/5 * * * ?";
		qm.addJob(name, group, FetchNodeJob.class, cronExpression);		
		qm.addOnceJob(name, group2, FetchNodeJob.class);
		name = "FetchMarketJob";
		cronExpression = "0 0/5 * * * ?";
		qm.addJob(name, group, FetchMarketJob.class, cronExpression);		
		qm.addOnceJob(name, group2, FetchMarketJob.class);
		qm.start();
	}
	
	public static DruidPlugin createDruidPlugin() {
		return null;
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		
	}
}
