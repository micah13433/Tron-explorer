package com.tron.explorer.service;

import org.tron.walletserver.WalletClient;

import com.tron.explorer.model.Account;
import com.tron.explorer.model.Accounts;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.PostParameter;
import com.tron.explorer.model.Transactions;
import com.tron.explorer.model.TronException;
import com.tron.explorer.util.PropUtil;

public class AccountService extends BaseService {
		
	public static Accounts queryAccounts(BaseQuery query) throws TronException{
		return new Accounts(client.get(
				PropUtil.getValue("baseNewURL") + "/api/account",
				new PostParameter[] { new PostParameter("limit", query.getLimit()),
					new PostParameter("start", query.getOffset()),
					new PostParameter("sort", query.getSort())}));
	}
	
	public static Account getAccountByAddress(String address) throws TronException{
		return  getAccountByAddress(address,true);
	}
	
	public static Account getAccountByAddress(String  address, boolean isMainnet) throws TronException{
		byte[] baAddress = WalletClient.decodeFromBase58Check(address);
	    if (baAddress == null) {
	       return null;
	    }
	    org.tron.protos.Protocol.Account account = WalletClient.queryAccount(baAddress,isMainnet);
		return new Account(account.toByteArray(),address);
	}
	
	public static long queryAccountsNum() throws TronException{
		return new Accounts(client.get(
				PropUtil.getValue("baseNewURL") + "/api/account?limit=0")).getTotalCount();		
	}
	
	public static Transactions queryTransactionList(String address,BaseQuery query) throws TronException{
		return queryTransactionList(address,query,null,true);
	}
	
	public static Transactions queryTransactionList(String address,BaseQuery query, String assetname, boolean isMainnet) throws TronException{
		String baseUrl = PropUtil.getValue("baseNewURL");
		if(!isMainnet){
			baseUrl = PropUtil.getValue("baseNewTestURL");
		}
		return new Transactions(client.get(
				baseUrl + "/api/transfer",
				new PostParameter[] { new PostParameter("address", address),
					new PostParameter("start", query.getOffset()),
					new PostParameter("count", "-true"),
					new PostParameter("limit", query.getLimit()),
					new PostParameter("sort", query.getSort())}),assetname);
	}
	
}
