package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.Constrain;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.StringUtils;
import com.tron.explorer.util.Utils;


public class Transactions  {
	
	private boolean success;
	private List<Transaction> transactions;
	private long count;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Transaction> getOrders() {
		return transactions;
	}
	public void setOrder(List<Transaction> orders) {
		this.transactions = orders;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public Transactions(String res) throws TronException{
		init(res);	
	}
	
	private void init(String inputString) throws TronException {
		transactions = new ArrayList<Transaction>();
		if(StringUtils.isEmpty(inputString)){
			return;
		}
		JSONObject obj = JSON.parseObject(inputString);
		if(obj.getString("total") == null) return;
		count = obj.getIntValue("total");
		JSONArray dataArray = obj.getJSONArray("data");
		if (dataArray == null || dataArray.size() <= 0)
			return;
		
		Transaction transaction = null;
		for (int i = 0; i < dataArray.size(); i++) {
			transaction = new Transaction();
			JSONObject job = dataArray.getJSONObject(i);
			transaction.setBlockHeight(job.getLongValue("block"));
			
			transaction.setTime(FormatUtil.formatTimeInMillis(job.getLongValue("timestamp")));			
			transaction.setSender(job.getString("transferFromAddress"));
			transaction.setRecipient(job.getString("transferToAddress"));
			if("TRX".equals(job.getString("tokenName"))){
				transaction.setType(Constrain.TRANSFERCONTRACT);
				transaction.setAmount(Utils.getStrictAmount(job.getLongValue("amount")));
			}else{
				transaction.setType(Constrain.TRANSFERASSETCONTRACT);
				transaction.setAmount(String.valueOf(job.getLongValue("amount")));
			}
			transactions.add(transaction);
		}
	} 
	

	
}
