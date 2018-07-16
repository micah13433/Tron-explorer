package com.tron.explorer.service;

import java.util.Optional;

import org.tron.api.GrpcAPI.WitnessList;
import org.tron.walletserver.WalletClient;

import com.tron.explorer.model.Delegates;
import com.tron.explorer.model.TronException;

public class DelegateService extends BaseService {
	
	public static Delegates queryDelegates(boolean isMainnet) throws TronException{
		Optional<WitnessList> witList = WalletClient.listWitnesses(isMainnet);
		if(!witList.isPresent()) return null;
		return new Delegates(witList.get());
	}
	
}
