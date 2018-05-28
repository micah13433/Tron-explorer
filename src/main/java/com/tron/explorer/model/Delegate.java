package com.tron.explorer.model;


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
