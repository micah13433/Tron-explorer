package com.tron.explorer.service;

import java.math.BigInteger;
import java.util.HashMap;

import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.protos.Contract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Contract.FreezeBalanceContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.UnfreezeBalanceContract;
import org.tron.protos.Protocol.Transaction;
import org.tron.walletserver.WalletClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.DecodeUtil;
import com.tron.explorer.util.StringUtils;

public class WalletService extends BaseService{
	
	public static Account getAccountByPassword(String password, boolean isMainnet) throws TronException{
		ECKey key = ECKey.fromPrivate(Hex.decode(password));
		String address = Base58.encodeChecked(key.getAddress());
		return 	AccountService.getAccountByAddress(address,isMainnet);
	}
	
	public static boolean send(String asset, String fromAddress,String toAddress, long amount, String password, boolean isMainnet) throws TronException {
		Transaction transaction = null;
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		byte[] toAddr = WalletClient.decodeFromBase58Check(toAddress);
		if(asset.equals("TRX")){
			TransferContract contract = WalletClient.createTransferContract(toAddr, owner, amount);
			if(!isMainnet){
				transaction = WalletClient.createTransactionTest4Transfer(contract);
			}else{
				transaction = WalletClient.createTransaction4Transfer(contract);
			}
			return signTransaction(transaction,password,isMainnet);
		}else{
			if(!isMainnet){
				transaction = WalletClient.createTransferAssetTestTransaction(toAddr,
						ByteArray.fromString(asset), owner,amount);
			}else{
				transaction = WalletClient.createTransferAssetTransaction(toAddr,
						ByteArray.fromString(asset), owner,amount);
			}
			return signTransaction(transaction,password,isMainnet);
		}	
		
	}
	
	public static boolean assetIssue(String fromAddress, String password, String name, long supply, int amount,int trxNum, String url,String desc, long startTime, long endTime, boolean isMainnet) throws TronException {
		AssetIssueContract contract = WalletClient.createAssetIssueContract(fromAddress, name,supply,amount,trxNum,url,desc,startTime,endTime);
		Transaction transaction;
		if(!isMainnet){
			transaction =  WalletClient.createAssetIssueTestTransaction(contract);
		}else{
			transaction =  WalletClient.createAssetIssueTransaction(contract);
		}		
		return signTransaction(transaction,password,isMainnet);
	}
	
	private static boolean signTransaction(Transaction transaction, String password) throws TronException {
		return signTransaction(transaction,password,true);
	}
	
	private static boolean signTransaction(Transaction transaction, String password, boolean isMainnet) throws TronException {
		boolean result = false;
		transaction = TransactionUtils.setTimestamp(transaction);
		BigInteger priK = new BigInteger(password, 16);
		transaction = TransactionUtils.sign(transaction, ECKey.fromPrivate(priK));		
		final byte[] bytes = ByteArray.fromHexString(DecodeUtil.bytesToHex(transaction.toByteArray()));
	    try {
	    	result = WalletClient.broadcastTransaction(bytes,isMainnet);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	    return result;
	}

	public static boolean vote(String fromAddress,String password, HashMap<String, String> witness, boolean isMainnet) throws TronException {
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		Transaction transaction;
		if(!isMainnet){
			transaction = WalletClient.createVoteWitnessTestTransaction(owner, witness);
		}else{
			transaction =  WalletClient.createVoteWitnessTransaction(owner, witness);
		}
		return signTransaction(transaction,password,isMainnet);
	}
	
	public static boolean assetBuy(String assetName, String fromAddress,String toAddress, long amount, String password, boolean isMainnet) throws TronException {
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		byte[] toAddr = WalletClient.decodeFromBase58Check(toAddress);
		Transaction transaction;
		if(!isMainnet){
			 transaction = WalletClient.participateAssetIssueTestTransaction(toAddr,
					 assetName.getBytes(), owner,amount);
		}else{
			 transaction = WalletClient.participateAssetIssueTransaction(toAddr,
					 assetName.getBytes(), owner,amount);			
		}	
		return signTransaction(transaction,password,isMainnet);
	}

	public static boolean freeze(String fromAddress, long frozen_balance,
			long frozenDuration, String password, boolean isMainnet) throws TronException {
		Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
		builder.setOwnerAddress(ByteString.copyFrom(WalletClient.decodeFromBase58Check(fromAddress))).setFrozenBalance(frozen_balance).setFrozenDuration(frozenDuration);
		FreezeBalanceContract  contract = builder.build();
		if(!isMainnet){
			Transaction transaction = WalletClient.freezeTestBalance(contract);
			return signTransaction(transaction,password,isMainnet);
		}else{
			Transaction transaction = WalletClient.freezeBalance(contract);
			return signTransaction(transaction,password);
		}		
	}
	
	public static boolean unfreeze(String fromAddress,String password, boolean isMainnet) throws TronException {
		Contract.UnfreezeBalanceContract.Builder builder = Contract.UnfreezeBalanceContract
		        .newBuilder();
		builder.setOwnerAddress(ByteString.copyFrom(WalletClient.decodeFromBase58Check(fromAddress)));
		UnfreezeBalanceContract  contract = builder.build();
		if(!isMainnet){
			Transaction transaction = WalletClient.unfreezeTestBalance(contract);
			return signTransaction(transaction,password,isMainnet);
		}else{
			Transaction transaction = WalletClient.unfreezeBalance(contract);
			return signTransaction(transaction,password);
		}		
	}

	public static boolean getFreeTRX(String fromAddress) throws TronException {
		boolean result = false;
		JSONObject obj = new JSONObject();
		obj.put("address", fromAddress);
		String response = client.post("https://tronscan.org/request-coins",obj);		
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(jsonObject.getBooleanValue("success")){
			result = true;
		}
		return result;
	}

	public static String getCurrPrice() throws TronException {
		String tronPrice = CacheKit.get("persistedList", "tronPrice");
		if(StringUtils.isBlank(tronPrice)){
			String response = client.get("https://api.coinmarketcap.com/v1/ticker/tron/");
			JSONArray array = JSONArray.parseArray(response);
			JSONObject jsonObject = array.getJSONObject(0);
			CacheKit.put("persistedList", "tronPrice", jsonObject.getString("price_usd"));
			tronPrice = jsonObject.getString("price_usd");
		}
		return tronPrice;
	}
}
