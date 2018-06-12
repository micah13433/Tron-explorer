package com.tron.explorer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Account.Vote;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.Utils;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private int type;
	private String typeDesc;
	private String desc;
	private long blockHeight;
	private String blockHash;
	private String sender;
	private String recipient;
	private String amount;	
	private String time;
	private String asset;
	private String hash;
	private boolean confirmed;
	private int frozenDuration;
	private String startTime;
	private String endTime;
	private String price;
	private Map<String, Long> assets;
	private List<Vote> votes;
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public int getFrozenDuration() {
		return frozenDuration;
	}

	public void setFrozenDuration(int frozenDuration) {
		this.frozenDuration = frozenDuration;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public int getType() {
		return type;
	}
	
	public String getTypeDesc() {
		return structTypeDesc(type);
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
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

	public String getPrice() {
		return price + " TRX";
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Map<String, Long> getAssets() {
		return assets;
	}

	public void setAssets(Map<String, Long> assets) {
		this.assets = assets;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	private String structTypeDesc(int type) {
		switch(type){
		case Constrain.ACCOUNTCREATECONTRACT: 
			return ("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.accountcreate\">用户创建</button>");
		case Constrain.TRANSFERASSETCONTRACT:
			return ("<button type=\"button\" class=\"btn btn-info\" i18n=\"transtype.transferasset\">资产转账</button>");
		case Constrain.VOTEASSETCONTRACT:
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.voteasset\">资产投票</button>");
		case Constrain.VOTEWITNESSCONTRACT: 
			return ("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.votewitness\">见证人投票</button>");
		case Constrain.WITNESSCREATECONTRACT: 
			return ("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.witnesscreate\">晋升见证人</button>");
		case Constrain.ASSETISSUECONTRACT: 
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.assetissue\">资产发布</button>");
		case Constrain.DEPLOYCONTRACT: 
			return ("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.depoly\">合约部署</button>");
		case Constrain.WITNESSUPDATECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.witnessupdate\">见证人更新</button>");
		case Constrain.PARTICIPATEASSETISSUECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-info\" i18n=\"transtype.assetjoin\">资产购买</button>");
		case Constrain.ACCOUNTUPDATECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.accountupdate\">账户更新</button>");
		case Constrain.FREEZEBALANCECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.freezebalance\">余额冻结</button>");
		case Constrain.UNFREEZEBALANCECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.unfreezebalance\">余额解冻</button>");
		case Constrain.WITHDRAWBALANCECONTRACT:
			return ("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.withdrawbalance\">奖励赎回</button>");
		case Constrain.UNFREEZEASSETCONTRACT:
			return ("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.unfreezeasset\">资产解冻</button>");
		case Constrain.UPDATEASSETCONTRACT:
			return ("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.updateasset\">资产更新</button>");
		}
		return ("<button type=\"button\" class=\"btn btn-primary\" i18n=\"transtype.tranfer\">普通转账</button>");		
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

	public Transaction(String res) throws TronException {
		init(JSONObject.parseObject(res));
	}

	
	public Transaction() {		
	}

	private void init(JSONObject json) throws TronException {
		try {
			hash = json.getString("hash");
			blockHeight = json.getLongValue("block");
			time =  FormatUtil.formatTimeInMillis(json.getLongValue("timestamp"));
			confirmed = json.getBooleanValue("confirmed");
			type = json.getIntValue("contractType");
			sender = json.getString("ownerAddress");
			JSONObject data = json.getJSONObject("contractData");
			
			if(type == Constrain.TRANSFERCONTRACT){
				recipient = data.getString("to");
				amount = String.valueOf(Utils.getStrictAmount(data.getLongValue("amount")));
			}else if(type == Constrain.TRANSFERASSETCONTRACT){
				recipient = data.getString("to");
				amount = data.getString("amount");
				asset = data.getString("token");
			}else if(type == Constrain.FREEZEBALANCECONTRACT){
				amount = String.valueOf(Utils.getStrictAmount(data.getLongValue("frozenBalance")));
				frozenDuration = data.getIntValue("frozenDuration");
			}else if(type == Constrain.ASSETISSUECONTRACT){
				amount = data.getString("totalSupply");
				startTime = FormatUtil.formatTimeInMillis(data.getLongValue("startTime"));
				endTime = FormatUtil.formatTimeInMillis(data.getLongValue("endTime"));
				asset = data.getString("name");
				price = data.getString("trxNum");
			}else if(type == Constrain.VOTEWITNESSCONTRACT){
				votes = new ArrayList<Vote>();
				JSONArray voteArray = data.getJSONArray("votes");
				Vote vote = null;
				Account account = new Account();
				for(int i=0; i< voteArray.size();i++){
					vote = account.new Vote();
					vote.setAddress(voteArray.getJSONObject(i).getString("voteAddress"));
					vote.setCount(voteArray.getJSONObject(i).getLongValue("voteCount"));
					votes.add(vote);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new TronException(ex.getMessage() + ":" + json.toString(), ex);
		}

	}
}
