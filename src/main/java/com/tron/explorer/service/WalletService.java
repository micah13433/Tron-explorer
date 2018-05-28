package com.tron.explorer.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.protos.Contract;
import org.tron.protos.Contract.FreezeBalanceContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.UnfreezeBalanceContract;
import org.tron.protos.Protocol.Transaction;
import org.tron.walletserver.WalletClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.model.Account;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.DecodeUtil;
import com.tron.explorer.util.PropUtil;

public class WalletService extends BaseService{
	
	public static Account getAccountByPassword(String password) throws TronException{
		ECKey key = ECKey.fromPrivate(Hex.decode(password));
		String address = Base58.encodeChecked(key.getAddress());
		return 	AccountService.getAccountByAddress(address);
	}
	
	public static boolean send(String asset, String fromAddress,String toAddress, long amount, String password) throws TronException {
		Transaction transaction = null;
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		byte[] toAddr = WalletClient.decodeFromBase58Check(toAddress);
		if(asset.equals("TRX")){
			TransferContract contract = WalletClient.createTransferContract(toAddr, owner, amount);
			transaction = WalletClient.createTransaction4Transfer(contract);
		}else{
			transaction = WalletClient.createTransferAssetTransaction(toAddr,
					ByteArray.fromString(asset), owner,amount);
		}	
		return signTransaction(transaction,password);
	}

	private static boolean signTransaction(Transaction transaction, String password) throws TronException {
		boolean result = false;
		transaction = TransactionUtils.setTimestamp(transaction);
		BigInteger priK = new BigInteger(password, 16);
		transaction = TransactionUtils.sign(transaction, ECKey.fromPrivate(priK));		
		final byte[] bytes = ByteArray.fromHexString(DecodeUtil.bytesToHex(transaction.toByteArray()));
	    try {
	    	result = WalletClient.broadcastTransaction(bytes);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	    return result;
	}

	public static boolean vote(String fromAddress,String password, HashMap<String, String> witness) throws TronException {
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		Transaction transaction =  WalletClient.createVoteWitnessTransaction(owner, witness);
		return signTransaction(transaction,password);
	}
	
	public static boolean assetBuy(String assetName, String fromAddress,String toAddress, long amount, String password) throws TronException {
		byte[] owner = WalletClient.decodeFromBase58Check(fromAddress);
		byte[] toAddr = WalletClient.decodeFromBase58Check(toAddress);
		Transaction transaction = WalletClient.participateAssetIssueTransaction(toAddr,
    		  ByteArray.fromHexString(assetName), owner,amount);
		return signTransaction(transaction,password);
	}

	public static boolean freeze(String fromAddress, long frozen_balance,
			long frozenDuration, String password) throws TronException {
		Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
		builder.setOwnerAddress(ByteString.copyFrom(WalletClient.decodeFromBase58Check(fromAddress))).setFrozenBalance(frozen_balance).setFrozenDuration(frozenDuration);
		FreezeBalanceContract  contract = builder.build();
		Transaction transaction = WalletClient.freezeBalance(contract);
		return signTransaction(transaction,password);
	}
	
	public static boolean unfreeze(String fromAddress,String password) throws TronException {
		Contract.UnfreezeBalanceContract.Builder builder = Contract.UnfreezeBalanceContract
		        .newBuilder();
		builder.setOwnerAddress(ByteString.copyFrom(WalletClient.decodeFromBase58Check(fromAddress)));
		UnfreezeBalanceContract  contract = builder.build();
		Transaction transaction = WalletClient.unfreezeBalance(contract);
		return signTransaction(transaction,password);
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
}
