package com.tron.explorer.model;

import com.tron.explorer.util.Utils;


public class Delegate  {
	
	private String address;
	private String url;
	private long producedTotal;
	private long missedTotal;
	private long votes;
	private long latestBlockNumber;
	private long latestSlotNum;
	private String pubKey;
	private boolean isJobs;
	private String productivity;
	private boolean status;
	
		
	public String getProductivity() {
		if(producedTotal > 0){
			return Utils.getPercentAmount(producedTotal, producedTotal + missedTotal);
		}else{
			return "";
		}
	}

	public void setProductivity(String productivity) {
		this.productivity = productivity;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUrl() {		
		return url.length() > 60 ? url.substring(0,60) + "..." : url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getProducedTotal() {
		return producedTotal;
	}

	public void setProducedTotal(long producedTotal) {
		this.producedTotal = producedTotal;
	}

	public long getMissedTotal() {
		return missedTotal;
	}

	public void setMissedTotal(long missedTotal) {
		this.missedTotal = missedTotal;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long votes) {
		this.votes = votes;
	}

	public long getLatestBlockNumber() {
		return latestBlockNumber;
	}

	public void setLatestBlockNumber(long latestBlockNumber) {
		this.latestBlockNumber = latestBlockNumber;
	}

	public long getLatestSlotNum() {
		return latestSlotNum;
	}

	public void setLatestSlotNum(long latestSlotNum) {
		this.latestSlotNum = latestSlotNum;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public boolean isJobs() {
		return isJobs;
	}

	public void setJobs(boolean isJobs) {
		this.isJobs = isJobs;
	}

	public Delegate(){
		super();
	}		
	
}
