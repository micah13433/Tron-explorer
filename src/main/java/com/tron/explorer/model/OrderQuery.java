package com.tron.explorer.model;

public class OrderQuery  extends BaseQuery {
	
	private String blockId;
    private int type;
    private String senderPublicKey;
    private String ownerPublicKey;
    private String ownerAddress;
    private String senderId;
    private String recipientId;
    private String amount;
    private String free;
	public String getBlockId() {
		return blockId;
	}
	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSenderPublicKey() {
		return senderPublicKey;
	}
	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}
	public String getOwnerPublicKey() {
		return ownerPublicKey;
	}
	public void setOwnerPublicKey(String ownerPublicKey) {
		this.ownerPublicKey = ownerPublicKey;
	}
	public String getOwnerAddress() {
		return ownerAddress;
	}
	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getFree() {
		return free;
	}
	public void setFree(String free) {
		this.free = free;
	}
    
}
