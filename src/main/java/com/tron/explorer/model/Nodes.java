package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	@SuppressWarnings("unchecked")
	private void init(String inputString) throws TronException {
		if (StringUtils.isEmpty(inputString)) {
			return;
		}
		inputString = inputString.replace("\\", "");
		inputString = inputString.substring(1, inputString.length() - 1);
		JSONObject obj = JSON.parseObject(inputString);
		JSONArray cityArray = obj.getJSONArray("citys");
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
				continue;
			}
			node.setCity(job.getString("city"));
			node.setCountry(job.getString("country"));
			node.setLatitude(job.getString("latitude"));
			node.setLongitude(job.getString("longitude"));
			node.setCount(Integer.valueOf(job.getString("count")));
			totalCount += node.getCount();
			nodes.add(node);

			//for chart
			if (!countryNodeMap.containsKey(job.getString("country"))) {
				countryNodeMap.put(job.getString("country"),Integer.valueOf(job.getString("count")));
			} else {
				countryNodeMap.put(job.getString("country"),countryNodeMap.get(job.getString("country")) + Integer.valueOf(job.getString("count")));
			}
			if (!cityNodeMap.containsKey(job.getString("country"))) {
				cityNodeMap.put(job.getString("country"),new HashMap<String, Integer>() {
						{
							put(job.getString("city"),Integer.valueOf(job.getString("count")));
						}
					});
			}else{
				Map<String, Integer> innerMap = cityNodeMap.get(job.getString("country"));
				innerMap.put(job.getString("city"), Integer.valueOf(job.getString("count")));
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
		Collections.sort(nodes, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Node j1 = (Node) o1;
				Node j2 = (Node) o2;
				int index = Integer.valueOf(j2.getCount()).compareTo(
						Integer.valueOf(j1.getCount()));
				return index;
			}
		});
	}

}
