package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.util.StringUtils;


public class Accounts  {
	
	private boolean success;
	private List<Account> accounts;
	private int totalCount;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public Accounts(String res) throws TronException{
		super();
		init(res);		
	} 
	
	private void init(String inputString) throws TronException {
		accounts = new ArrayList<Account>();
		if(StringUtils.isEmpty(inputString)){
			return;
		}
		JSONObject obj = JSON.parseObject(inputString);
		if(obj.getString("total") == null) return;
		totalCount = obj.getIntValue("total");
		CacheKit.put("persistedList", "accountNum", totalCount);
		JSONArray dataArray = obj.getJSONArray("data");
		if (dataArray == null || dataArray.size() <= 0)
			return;
		
		Account account = null;
		for (int i = 0; i < dataArray.size(); i++) {
			account = new Account();
			JSONObject job = dataArray.getJSONObject(i);
			account.setAddress(job.getString("address"));
			account.setBalance(job.getLongValue("balance"));
			accounts.add(account);
		}
		
	} 
	
}
