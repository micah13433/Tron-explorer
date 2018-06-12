package com.tron.explorer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tron.common.utils.Sha256Hash;
import org.tron.protos.Contract.AccountCreateContract;
import org.tron.protos.Contract.AccountUpdateContract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Contract.DeployContract;
import org.tron.protos.Contract.FreezeBalanceContract;
import org.tron.protos.Contract.ParticipateAssetIssueContract;
import org.tron.protos.Contract.TransferAssetContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.UnfreezeAssetContract;
import org.tron.protos.Contract.UnfreezeBalanceContract;
import org.tron.protos.Contract.UpdateAssetContract;
import org.tron.protos.Contract.VoteAssetContract;
import org.tron.protos.Contract.VoteWitnessContract;
import org.tron.protos.Contract.WithdrawBalanceContract;
import org.tron.protos.Contract.WitnessCreateContract;
import org.tron.protos.Contract.WitnessUpdateContract;
import org.tron.protos.Protocol.BlockHeader.raw;
import org.tron.protos.Protocol.Transaction.Contract;
import org.tron.walletserver.WalletClient;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tron.explorer.Constrain;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.util.DecodeUtil;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.RelativeDateFormatUtil;
import com.tron.explorer.util.Utils;

public class Block implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private boolean confirmed = false;
	private int size;
	private long height;
	private int status;
	private String hash;
	private String previousHash;
	private long generatorId;
	private String generatorAddress;
	private String generateTime;
	private String generateTimeAgoDesc;	
	private String reward;
	private String txTrieRoot;
	private int numberOfTransactions;
	private String blockSignature;
	private List<Transaction> tradeList;
	
	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public int getSize() {
		return size;
	}	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public long getGeneratorId() {
		return generatorId;
	}

	public void setGeneratorId(long generatorId) {
		this.generatorId = generatorId;
	}

	public String getGeneratorAddress() {
		return generatorAddress;
	}

	public void setGeneratorAddress(String generatorAddress) {
		this.generatorAddress = generatorAddress;
	}

	public String getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(String generateTime) {
		this.generateTime = generateTime;
	}

	public String getGenerateTimeAgoDesc() {
		return generateTimeAgoDesc;
	}

	public void setGenerateTimeAgoDesc(String generateTimeAgoDesc) {
		this.generateTimeAgoDesc = generateTimeAgoDesc;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getTxTrieRoot() {
		return txTrieRoot;
	}

	public void setTxTrieRoot(String txTrieRoot) {
		this.txTrieRoot = txTrieRoot;
	}

	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

	public String getBlockSignature() {
		return blockSignature;
	}

	public void setBlockSignature(String blockSignature) {
		this.blockSignature = blockSignature;
	}	
	
	public List<Transaction> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<Transaction> tradeList) {
		this.tradeList = tradeList;
	}

	public Block(byte[] array, boolean isShowDetail) throws TronException {
		super();
		init(array,isShowDetail);
	}
	
	private void init(byte[] array, boolean isShowDetail) throws TronException {
		tradeList = new ArrayList<Transaction>();
		try {			
			size = array.length;
			org.tron.protos.Protocol.Block block = org.tron.protos.Protocol.Block.parseFrom(array);			
			height = block.getBlockHeader().getRawData().getNumber();
			long lastConfrimedHeight = WalletClient.getLastestConfirmedBlockByLatestNum();
			if(lastConfrimedHeight >= height){
				confirmed = true;
			}else{
				confirmed = false;
			}
			raw rawData = block.getBlockHeader().getRawData();
			hash = Sha256Hash.of(rawData.toByteArray()).toString();
			generatorAddress = Base58.encodeChecked(rawData.getWitnessAddress().toByteArray());
			generateTime = FormatUtil.formatTimeInMillis(rawData.getTimestamp());
			numberOfTransactions = block.getTransactionsCount();
			if(isShowDetail){
				List<org.tron.protos.Protocol.Transaction> transList = block.getTransactionsList();
				if(transList != null && transList.size() > 0){
					Transaction trade = null;
					for(org.tron.protos.Protocol.Transaction transaction : transList){
						trade = new Transaction();
						trade.setHash(Sha256Hash.of(transaction.getRawData().toByteArray()).toString());
						trade.setBlockHeight(height);
						structTrade(transaction,trade);
						tradeList.add(trade);
					}
				}
				previousHash = DecodeUtil.bytesToHex(rawData.getParentHash().toByteArray());
				generatorId = rawData.getWitnessId();
				generateTimeAgoDesc = RelativeDateFormatUtil.format(new Date(rawData.getTimestamp()));

			}
//			txTrieRoot = DecodeUtil.bytesToHex(rawData.getTxTrieRoot().toByteArray());
//			blockSignature = DecodeUtil.bytesToHex(block.getBlockHeader().getWitnessSignature().toByteArray());
		} catch (Exception ex) {
			throw new TronException(ex.getMessage() + ":" + ex.toString(), ex);
		}
	}

	private void structTrade(org.tron.protos.Protocol.Transaction transaction,
			Transaction trade) throws InvalidProtocolBufferException {
		List<Contract> contractList = transaction.getRawData().getContractList();
		for(Contract contract : contractList){
			int type = contract.getTypeValue();
			Any any = contract.getParameter();
			trade.setTime(FormatUtil.formatTimeInMillis(transaction.getRawData().getTimestamp()));
			switch(type){
			case Constrain.ACCOUNTCREATECONTRACT: 
				trade.setType(Constrain.ACCOUNTCREATECONTRACT);
				trade.setDesc("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.accountcreate\">用户创建</button>");
				AccountCreateContract accountCreateContract = any.unpack(AccountCreateContract.class);
				trade.setSender(Base58.encodeChecked(accountCreateContract.getOwnerAddress().toByteArray()));
				break;
			case Constrain.TRANSFERASSETCONTRACT:
				trade.setType(Constrain.TRANSFERASSETCONTRACT);
				trade.setDesc("<button type=\"button\" class=\"btn btn-info\" i18n=\"transtype.transferasset\">资产转账</button>");
				TransferAssetContract transferAssetContract = any.unpack(TransferAssetContract.class);
				trade.setAmount(String.valueOf(transferAssetContract.getAmount()));
				trade.setSender(Base58.encodeChecked(transferAssetContract.getOwnerAddress().toByteArray()));
				trade.setRecipient(Base58.encodeChecked(transferAssetContract.getToAddress().toByteArray()));
				trade.setAsset(DecodeUtil.bytesToHex(transferAssetContract.getAssetName().toByteArray()));
				break;
			case Constrain.VOTEASSETCONTRACT:
				trade.setType(Constrain.VOTEASSETCONTRACT);
				VoteAssetContract voteAssetContract = any.unpack(VoteAssetContract.class);
				trade.setSender(Base58.encodeChecked(voteAssetContract.getOwnerAddress().toByteArray()));
				trade.setAmount(String.valueOf(voteAssetContract.getCount()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.voteasset\">资产投票</button>");
				break;
			case Constrain.VOTEWITNESSCONTRACT: 
				trade.setType(Constrain.VOTEWITNESSCONTRACT);
				VoteWitnessContract voteWitnessContract = any.unpack(VoteWitnessContract.class);
				trade.setSender(Base58.encodeChecked(voteWitnessContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.votewitness\">见证人投票</button>");
				break;
			case Constrain.WITNESSCREATECONTRACT: 
				trade.setType(Constrain.WITNESSCREATECONTRACT);
				WitnessCreateContract witnessCreateContract = any.unpack(WitnessCreateContract.class);
				trade.setSender(Base58.encodeChecked(witnessCreateContract.getOwnerAddress().toByteArray()));
//				trade.setRecipient(DecodeUtil.bytesToHex(witnessCreateContract.getUrl().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.witnesscreate\">晋升见证人</button>");
				break;
			case Constrain.ASSETISSUECONTRACT: 
				trade.setType(Constrain.ASSETISSUECONTRACT);
				AssetIssueContract assetIssueContract = any.unpack(AssetIssueContract.class);
				trade.setSender(Base58.encodeChecked(assetIssueContract.getOwnerAddress().toByteArray()));
				trade.setAmount(String.valueOf(assetIssueContract.getTotalSupply()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.assetissue\">资产发布</button>");
				break;
			case Constrain.DEPLOYCONTRACT: 
				trade.setType(Constrain.DEPLOYCONTRACT);
				DeployContract deployContract = any.unpack(DeployContract.class);
				trade.setSender(Base58.encodeChecked(deployContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.depoly\">合约部署</button>");
				break;
			case Constrain.WITNESSUPDATECONTRACT:
				trade.setType(Constrain.WITNESSUPDATECONTRACT);
				WitnessUpdateContract witnessUpdateContract = any.unpack(WitnessUpdateContract.class);
				trade.setSender(Base58.encodeChecked(witnessUpdateContract.getOwnerAddress().toByteArray()));
//				trade.setRecipient(DecodeUtil.bytesToHex(witnessUpdateContract.getUpdateUrl().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.witnessupdate\">见证人更新</button>");
				break;
			case Constrain.PARTICIPATEASSETISSUECONTRACT:
				trade.setType(Constrain.PARTICIPATEASSETISSUECONTRACT);
				ParticipateAssetIssueContract participateAssetIssueContract = any.unpack(ParticipateAssetIssueContract.class);
				trade.setSender(Base58.encodeChecked(participateAssetIssueContract.getOwnerAddress().toByteArray()));
				trade.setAmount(String.valueOf(participateAssetIssueContract.getAmount()));
				trade.setRecipient(Base58.encodeChecked(participateAssetIssueContract.getToAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-info\" i18n=\"transtype.assetjoin\">资产购买</button>");
				break;
			case Constrain.ACCOUNTUPDATECONTRACT:
				trade.setType(Constrain.ACCOUNTUPDATECONTRACT);
				AccountUpdateContract accountUpdateContract = any.unpack(AccountUpdateContract.class);
				trade.setSender(Base58.encodeChecked(accountUpdateContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.accountupdate\">账户更新</button>");
				break;
			case Constrain.FREEZEBALANCECONTRACT:
				trade.setType(Constrain.FREEZEBALANCECONTRACT);
				FreezeBalanceContract freezeBalanceContract = any.unpack(FreezeBalanceContract.class);
				trade.setSender(Base58.encodeChecked(freezeBalanceContract.getOwnerAddress().toByteArray()));
				trade.setAmount(String.valueOf(freezeBalanceContract.getFrozenBalance() / 1000000));
				trade.setDesc("<button type=\"button\" class=\"btn btn-success\" i18n=\"transtype.freezebalance\">余额冻结</button>");
				break;
			case Constrain.UNFREEZEBALANCECONTRACT:
				trade.setType(Constrain.UNFREEZEBALANCECONTRACT);
				UnfreezeBalanceContract unFreezeBalanceContract = any.unpack(UnfreezeBalanceContract.class);
				trade.setSender(Base58.encodeChecked(unFreezeBalanceContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.unfreezebalance\">余额解冻</button>");
				break;
			case Constrain.WITHDRAWBALANCECONTRACT:
				trade.setType(Constrain.WITHDRAWBALANCECONTRACT);
				WithdrawBalanceContract withdrawBalanceContract = any.unpack(WithdrawBalanceContract.class);
				trade.setSender(Base58.encodeChecked(withdrawBalanceContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.withdrawbalance\">奖励赎回</button>");
				break;
			case Constrain.UNFREEZEASSETCONTRACT:
				trade.setType(Constrain.UNFREEZEASSETCONTRACT);
				UnfreezeAssetContract unfreezeAssetContract = any.unpack(UnfreezeAssetContract.class);
				trade.setSender(Base58.encodeChecked(unfreezeAssetContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-warning\" i18n=\"transtype.unfreezeasset\">资产解冻</button>");
				break;
			case Constrain.UPDATEASSETCONTRACT:
				trade.setType(Constrain.UPDATEASSETCONTRACT);
				UpdateAssetContract updateAssetContract = any.unpack(UpdateAssetContract.class);
				trade.setSender(Base58.encodeChecked(updateAssetContract.getOwnerAddress().toByteArray()));
				trade.setDesc("<button type=\"button\" class=\"btn btn-danger\" i18n=\"transtype.updateasset\">资产更新</button>");
				break;
			case Constrain.TRANSFERCONTRACT:  
				trade.setType(Constrain.TRANSFERCONTRACT);
				trade.setDesc("<button type=\"button\" class=\"btn btn-primary\" i18n=\"transtype.tranfer\">普通转账</button>");
				TransferContract transferContract = any.unpack(TransferContract.class);
				trade.setAmount(Utils.getStrictAmount(transferContract.getAmount()));
				trade.setSender(Base58.encodeChecked(transferContract.getOwnerAddress().toByteArray()));
				trade.setRecipient(Base58.encodeChecked(transferContract.getToAddress().toByteArray()));
				trade.setAsset("TRX");
				break;
			}
		}
	}

}
