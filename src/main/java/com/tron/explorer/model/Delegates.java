package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;

import org.tron.api.GrpcAPI.WitnessList;
import org.tron.protos.Protocol.Witness;

import com.tron.explorer.Constrain;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.service.BlockService;
import com.tron.explorer.util.DecodeUtil;


public class Delegates  {
	
	private boolean success;
	private List<Delegate> delegates;
	private int totalCount;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Delegate> getDelegates() {
		return delegates;
	}
	public void setDelegates(List<Delegate> delegates) {
		this.delegates = delegates;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public Delegates() throws TronException{
		delegates = new ArrayList<Delegate>();	
	} 
	
	public Delegates(WitnessList witnessList) throws TronException{
		super();
		init(witnessList);		
	} 
	
	private void init(WitnessList witnessList) throws TronException {
		delegates = new ArrayList<Delegate>();
		totalCount = witnessList.getWitnessesCount();
		try {			
			List<Witness> list = witnessList.getWitnessesList();
			Delegate delegate = null;
			Witness wit = null;
			long maxBlockHeight = BlockService.queryLatestConfirmedBlock();
			for(int i=0; i< list.size(); i++){
				wit = list.get(i);
				if(wit == null) continue;

				delegate = new Delegate();
				delegate.setAddress(Base58.encodeChecked(wit.getAddress().toByteArray()));
				delegate.setUrl(wit.getUrl());
				delegate.setVotes(wit.getVoteCount());
				delegate.setLatestBlockNumber(wit.getLatestBlockNum());
				if(maxBlockHeight - wit.getLatestBlockNum() <= Constrain.delegatePageSize){
					delegate.setStatus(true);
				}else{
					delegate.setStatus(false);
				}
				delegate.setLatestSlotNum(wit.getLatestSlotNum());
				delegate.setMissedTotal(wit.getTotalMissed());
				delegate.setProducedTotal(wit.getTotalProduced());
				delegate.setPubKey(DecodeUtil.bytesToHex(wit.getPubKey().toByteArray()));
				delegate.setJobs(wit.getIsJobs());
				delegates.add(delegate);
			}
			
		} catch (Exception ex) {
			throw new TronException(ex.getMessage() + ":" + ex.toString(), ex);
		}
	} 
	
}
