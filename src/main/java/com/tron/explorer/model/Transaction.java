package com.tron.explorer.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.Constrain;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private int type;
	private String desc;
	private long blockHeight;
	private String blockHash;
	private String sender;
	private String recipient;
	private String amount;	
	private String time;
	private String asset;	
	
	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(long blockHeight) {
		this.blockHeight = blockHeight;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getAmount() {
		return format(amount);
	}

	private String format(String amount) {
		String result = String.valueOf(amount);
		if(this.type == Constrain.ACCOUNTCREATECONTRACT || this.type == Constrain.VOTEASSETCONTRACT || this.type == Constrain.VOTEWITNESSCONTRACT || this.type == Constrain.WITNESSCREATECONTRACT || this.type == Constrain.DEPLOYCONTRACT || this.type == Constrain.WITNESSUPDATECONTRACT || this.type == Constrain.ACCOUNTUPDATECONTRACT || this.type == Constrain.UNFREEZEBALANCECONTRACT || this.type == Constrain.WITHDRAWBALANCECONTRACT){
			result = "";
		}
		return result;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}	
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Transaction() throws TronException {
		super();
	}

	
	private void init(JSONObject json) throws TronException {
//		if(json!=null){
//			try {
//				if(json.containsKey("transaction")){
//					json = json.getJSONObject("transaction");
//				}
//				id = json.getString("id");
//				blockId = json.getString("blockId");
//				timestamp = json.getLongValue("timestamp");
//				height = json.getString("height");
//				type = json.getIntValue("type");
//				senderPublicKey = json.getString("senderPublicKey");
//				senderId = json.getString("senderId");
//				recipientId = json.getString("recipientId");
//				amount = json.getString("amount");
//				fee = json.getString("fee");
//				signature = json.getString("signature");
//				signSignature = json.getString("signSignature");
//				signatures = json.getString("signatures");
//				confirmations = json.getString("confirmations");				
//				asset = json.getString("asset");
//
//			} catch (JSONException jsone) {
//				throw new TronException(jsone.getMessage() + ":" + json.toString(), jsone);
//			}
//		}
	}
}
