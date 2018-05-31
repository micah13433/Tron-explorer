package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tron.api.GrpcAPI.AccountNetMessage;
import org.tron.walletserver.WalletClient;

import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.util.CheckUtil;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.Utils;

public class Account  {
	
	public class Vote{
		private String address;
		private long count;
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
		
		Vote(){
			
		}
	}
	
	public class Frozen{
		private String expireTime;
		private long count;
		public String getExpireTime() {
			return expireTime;
		}
		public void setExpireTime(String expireTime) {
			this.expireTime = expireTime;
		}
		public String getCount() {
			return Utils.getStrictAmount(count);
		}
		
		public void setCount(long count) {
			this.count = count;
		}		
		
		Frozen(){
			
		}
	}
	private String address;
	private String name;
	private int type;
	private long balance;
	private Map<String, Long> assets;
	private List<Vote> votes;
	private List<Frozen> frozens;
	private String lastOperationTime;
	private long frozenAmount;
	private long bandWidthUsed;
	private long bandWidthRemain;
	
	public long getBandWidthRemain() {
		return bandWidthRemain;
	}

	public void setBandWidthRemain(long bandWidthTotal) {
		this.bandWidthRemain = bandWidthTotal;
	}

	public String getBandWidthUsed() {
		return String.valueOf(bandWidthUsed);
	}

	public void setBandWidthUsed(long bandWidth) {
		this.bandWidthUsed = bandWidth;
	}

	public String getFrozenAmount() {
		return Utils.getStrictAmount(frozenAmount);
	}

	public void setFrozenAmount(long frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBalance() {
		return Utils.getStrictAmount(balance);
	}

	public void setBalance(long balance) {
		this.balance = balance;
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
	
	public List<Frozen> getFrozens() {
		return frozens;
	}

	public void setFrozens(List<Frozen> frozens) {
		this.frozens = frozens;
	}

	public String getLastOperationTime() {
		return lastOperationTime;
	}

	public void setLastOperationTime(String lastOperationTime) {
		this.lastOperationTime = lastOperationTime;
	}

	public Account() throws TronException {
		super();
	}
	
	public Account(byte[] array, String fromAddress) throws TronException {
		super();
		votes = new ArrayList<Vote>();
		frozenAmount = 0;
		try {			
			org.tron.protos.Protocol.Account account = org.tron.protos.Protocol.Account.parseFrom(array);
			if(account == null){
				return;
			}
			List<org.tron.protos.Protocol.Account.Frozen> frozenList = account.getFrozenList();
			if(frozenList != null && frozenList.size() > 0){
				frozens = new ArrayList<Frozen>();
				Frozen frozen = null;
				for(org.tron.protos.Protocol.Account.Frozen fro : frozenList){
					frozen = new Frozen();
					frozen.setExpireTime(FormatUtil.formatTimeInMillis(fro.getExpireTime()));
					frozen.setCount(fro.getFrozenBalance());
					frozens.add(frozen);
					frozenAmount += fro.getFrozenBalance();
				}
			}
			address = Base58.encodeChecked(account.getAddress().toByteArray());
			if(!CheckUtil.isValidAddress(address)){
				address = fromAddress;
			}
			balance = account.getBalance();
			bandWidthUsed = account.getNetUsage();
			AccountNetMessage message = WalletClient.getAccountNet(account.getAddress().toByteArray());
			bandWidthRemain = message.getNetLimit() - message.getNetUsed();
			lastOperationTime = FormatUtil.formatTimeInMillis(account.getLatestOprationTime());
			assets = account.getAssetMap();
			if(account.getVotesList().size() > 0){
				Vote vote = null;
				for(org.tron.protos.Protocol.Vote oldVote : account.getVotesList()){
					vote = new Vote();
					vote.setAddress(Base58.encodeChecked(oldVote.getVoteAddress().toByteArray()));
					vote.setCount(oldVote.getVoteCount());
					votes.add(vote);
				}
			}
		} catch (Exception ex) {
			throw new TronException(ex.getMessage() + ":" + ex.toString(), ex);
		}
	}

}
