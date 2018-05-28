package com.tron.explorer.service;

import java.util.Optional;

import org.tron.api.GrpcAPI.AssetIssueList;
import org.tron.walletserver.WalletClient;

import com.tron.explorer.model.Assets;
import com.tron.explorer.model.TronException;

public class AssetService extends BaseService {
	
	public static Assets queryAsserts() throws TronException{
		Optional<AssetIssueList> assetList = WalletClient.getAssetIssueList();
		if(!assetList.isPresent()){
			return new Assets();
		}
		return new Assets(assetList.get());		
	}
}
