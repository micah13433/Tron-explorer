package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.util.StringUtils;

public class Nodes {

	private boolean success;
	private List<Node> nodes;
	private int totalCount;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Nodes(String string) throws TronException {
		init(string);
	}

	private void init(String inputString) throws TronException {
		if (StringUtils.isEmpty(inputString)) {
			return;
		}
		JSONObject obj = JSON.parseObject(inputString);
		JSONArray cityArray = obj.getJSONArray("nodes");
		if (cityArray == null || cityArray.size() <= 0)
			return;
		Node node = null;
		nodes = new ArrayList<Node>();
		totalCount = 0;
		List<Node4Chart> node4ChartList = new ArrayList<Node4Chart>();
		
		Map<String, Integer> countryNodeMap = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> cityNodeMap = new HashMap<String, Map<String, Integer>>();
		for (int i = 0; i < cityArray.size(); i++) {
			node = new Node();
			JSONObject job = cityArray.getJSONObject(i);
			if (StringUtils.isBlank(job.getString("city"))
					|| "null".equals(job.getString("city"))) {
				node.setCity("Unknown City");
			}else{
				node.setCity(job.getString("city"));
			}
			node.setCountry(job.getString("country"));
			node.setLatitude(job.getString("lat"));
			node.setLongitude(job.getString("lng"));
			node.setCount(1);
			node.setIp(job.getString("ip"));
			totalCount ++;
			nodes.add(node);

			//for chart
			if (!countryNodeMap.containsKey(job.getString("country"))) {
				countryNodeMap.put(job.getString("country"),1);
			} else {
				countryNodeMap.put(job.getString("country"),countryNodeMap.get(job.getString("country")) + 1);
			}
			if (!cityNodeMap.containsKey(job.getString("country"))) {
				cityNodeMap.put(job.getString("country"),new HashMap<String, Integer>() {
						{
							put(job.getString("city"),1);
						}
					});
			}else{
				Map<String, Integer> innerMap = cityNodeMap.get(job.getString("country"));
				if(innerMap.containsKey(job.getString("city"))){
					innerMap.put(job.getString("city"), innerMap.get(job.getString("city")) + 1);
				}else{
					innerMap.put(job.getString("city"), 1);
				}
				cityNodeMap.put(job.getString("country"),innerMap);
			}
		}
		Iterator it = countryNodeMap.entrySet().iterator();
		Node4Chart node4Chart;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			node4Chart = new Node4Chart();
			node4Chart.setCountry(String.valueOf(entry.getKey()));
			node4Chart.setCount((Integer)entry.getValue());
			node4Chart.setCity(cityNodeMap.get(String.valueOf(entry.getKey())));
			node4ChartList.add(node4Chart);
		}
		CacheKit.remove("nodeList", "second");
		CacheKit.put("nodeList", "second", node4ChartList);

	}

}
