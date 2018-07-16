package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.Constrain;
import com.tron.explorer.service.AssetService;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.Utils;


public class Asset {
	
	public class Top{
		private String address;
		private long count;
		private String rate;
		private long total;				
		
		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public String getRate() {
			return Utils.getPercentAmount(count, total,6);
		}

		public void setRate(String rate) {
			this.rate = rate;
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public Top(){			
		}
	}
	
	private String name;
	private String decodeName;
	private String ownerAddress;
	private long totalSupply;
	private String startTime;
	private String endTime;
	private long startTimeOfLong;
	private long endTimeOfLong;
	private String description;
	private long num;
	private long trxNum;
	private float price;
	private String url;
	private long holder;
	private List<Transaction> trans;
	private List<Top> tops;
	private boolean finised = false;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	public String getDecodeName() {
		return decodeName;
	}

	public void setDecodeName(String decodeName) {
		this.decodeName = decodeName;
	}

	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}

	public long getTotalSupply() {
		return totalSupply;
	}

	public void setTotalSupply(long totalSupply) {
		this.totalSupply = totalSupply;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getTrxNum() {
		return trxNum;
	}

	public void setTrxNum(long trxNum) {
		this.trxNum = trxNum;
	}

	public String getPrice() {
		return String.valueOf(price);
	}

	public void setPrice(float price) {
		this.price = price;
	}	
	
	public long getStartTimeOfLong() {
		return startTimeOfLong;
	}

	public void setStartTimeOfLong(long startTimeOfLong) {
		this.startTimeOfLong = startTimeOfLong;
	}

	public long getEndTimeOfLong() {
		return endTimeOfLong;
	}

	public void setEndTimeOfLong(long endTimeOfLong) {
		this.endTimeOfLong = endTimeOfLong;
	}

	public Asset() throws TronException {
		super();
	}
	
	public Asset(String res) throws TronException {
		super();
		init(res);
	}	
		
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getHolder() {
		return holder;
	}

	public void setHolder(long holder) {
		this.holder = holder;
	}	

	public List<Transaction> getTrans() {
		return trans;
	}

	public void setTrans(List<Transaction> trans) {
		this.trans = trans;
	}

	public List<Top> getTops() {
		return tops;
	}

	public void setTops(List<Top> tops) {
		this.tops = tops;
	}

	public boolean getFinised() {
		return finised;
	}

	public void setFinised(boolean isFinised) {
		this.finised = isFinised;
	}

	private void init(String inputString) throws TronException {
		if(StringUtils.isBlank(inputString)){
			return;
		}
		JSONObject asset = JSON.parseObject(inputString);
		totalSupply = asset.getLongValue("totalSupply");
		name = asset.getString("name");
		startTime = FormatUtil.formatTimeInMillis(asset.getLongValue("startTime"));
		endTime = FormatUtil.formatTimeInMillis(asset.getLongValue("endTime"));
		description = asset.getString("description");
		url = asset.getString("url");
		holder = asset.getLongValue("nrOfTokenHolders");
		price = Float.valueOf(1)/asset.getLongValue("num");
		ownerAddress = asset.getString("ownerAddress");
		
		JSONArray assetTrans = JSONObject.parseObject(AssetService.getAssetTrans(name)).getJSONArray("data");
		Transaction  transaction = null;
		trans = new ArrayList<Transaction>();
		tops = new ArrayList<Top>();
		for(int i=0; i<assetTrans.size(); i++){
			transaction = new Transaction();
			transaction.setSender(assetTrans.getJSONObject(i).getString("transferFromAddress"));
			transaction.setRecipient(assetTrans.getJSONObject(i).getString("transferToAddress"));
			transaction.setAmount(assetTrans.getJSONObject(i).getString("amount"));
			transaction.setTime(FormatUtil.formatTimeInMillis(assetTrans.getJSONObject(i).getLongValue("timestamp")));
			transaction.setType(Constrain.TRANSFERASSETCONTRACT);
			transaction.setHash(assetTrans.getJSONObject(i).getString("transactionHash"));
			trans.add(transaction);
		}
		JSONArray assetTops = JSONObject.parseObject(AssetService.getAssetTops(name)).getJSONArray("data");
		Top top = null;
		for(int j=0; j<assetTops.size(); j++){
			top = new Top();
			top.setAddress(assetTops.getJSONObject(j).getString("address"));
			top.setCount(assetTops.getJSONObject(j).getLongValue("balance"));
			top.setTotal(totalSupply);
			tops.add(top);
		}
	}
}
