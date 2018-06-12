package com.tron.explorer.service;

import com.tron.explorer.model.Transaction;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.PropUtil;

public class TransService extends BaseService {
	
	public static Transaction getTransactionByHash(String hash) throws TronException{
		return new Transaction(client.get(
				PropUtil.getValue("baseNewURL") + "/api/transaction/" + hash));		
	}
	
}
