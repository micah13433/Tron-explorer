package com.tron.explorer.service;

import com.tron.explorer.model.Nodes;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.PropUtil;

public class NodeService extends BaseService {
	
	public static Nodes queryNodes() throws TronException{
		return new Nodes(client.get(
				PropUtil.getValue("baseURL") + "/nodeList"));		
	}
	
	public static long queryNodesNum() throws TronException{
		return new Nodes(client.get(
				PropUtil.getValue("baseURL") + "/nodeList")).getTotalCount();
	}
	
}
