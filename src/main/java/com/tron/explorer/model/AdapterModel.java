package com.tron.explorer.model;

import org.tron.api.GrpcAPI.NumberMessage;

public class AdapterModel  {
	
	private static byte[] array;
	
	public AdapterModel(byte[] array){
		this.array = array;
	}
	
	public long getLastBlockNumber(){
		long result = 0L;
		try {	
			org.tron.protos.Protocol.Block block = org.tron.protos.Protocol.Block.parseFrom(array);			
			result = block.getBlockHeader().getRawData().getNumber();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public long getTransactionNum() {
		long result = 0L;
		try {	
			NumberMessage numMessage = NumberMessage.parseFrom(array);			
			result = numMessage.getNum();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
}
