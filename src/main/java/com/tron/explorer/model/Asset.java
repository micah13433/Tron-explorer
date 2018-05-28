package com.tron.explorer.model;


public class Asset {
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

	public float getPrice() {
		return price;
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
	
	private void init(String inputString) throws TronException {
		
	}
}
