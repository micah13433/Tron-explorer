package com.tron.explorer.controller;


import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Node;
import com.tron.explorer.model.Node4Chart;
import com.tron.explorer.model.Nodes;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.PageUtil;

public class NodeController extends Controller {
		
	public void index() throws TronException {
		
		Long currPageIndex = PageUtil.getPage(getPara("page"));		
		Nodes nodes = CacheKit.get("nodeList", "index");
		int [] pageIndexArray = PageUtil.getPageIndex(nodes.getNodes(),currPageIndex);
		List<Node> nodeList = nodes.getNodes().subList(pageIndexArray[0], pageIndexArray[1]);		
		setAttr("currpage",currPageIndex);
		setAttr("totalpage",nodes.getNodes().size()/Constrain.pageSize + 1);
		setAttr("nodes", nodeList);
		setAttr("count", nodes.getNodes().size());
		render("node/index.html");		
	}
	
	public void count() throws TronException{
		renderText(String.valueOf(CacheKit.get("persistedList", "nodeNum")));
	}
	
	public void chart() throws TronException{
		List<Node4Chart> node4ChartList = CacheKit.get("nodeList", "second");
		renderJson(node4ChartList);
	}
}
 