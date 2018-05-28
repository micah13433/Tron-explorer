package com.tron.explorer.service;

import java.util.List;
import java.util.Optional;

import org.tron.api.GrpcAPI.BlockList;
import org.tron.walletserver.WalletClient;

import com.tron.explorer.Constrain;
import com.tron.explorer.model.AdapterModel;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.Block;
import com.tron.explorer.model.Blocks;
import com.tron.explorer.model.TronException;

public class BlockService extends BaseService {
	
	public static Block getBlockByHeight(long num) throws TronException{
		return getBlockByHeight(num,true);
	}
	
	public static Block getBlockByHeight(long num,boolean isShowDetail) throws TronException{
		org.tron.protos.Protocol.Block currentBlock = WalletClient.GetBlock(num);
		return new Block(currentBlock.toByteArray(),isShowDetail);
	}
	
	public static long getLatestBlockNumber() throws TronException{
	    org.tron.protos.Protocol.Block currentBlock = WalletClient.GetBlock(-1);
	    if(currentBlock == null) {
	    	return 0;
	    }
		return new AdapterModel(currentBlock.toByteArray()).getLastBlockNumber();
	}
	
	public static Blocks queryBlocks(BaseQuery query) throws TronException{
		
		Optional<BlockList> blockList =  WalletClient.getBlockByLimitNext(query.getOffset(), query.getEndset());
		return new Blocks(blockList.get());
	}
	
	public static List<Block> queryLatestBlocks() throws TronException{	
		Optional<BlockList> blockList =  WalletClient.getBlockByLatestNum(Constrain.INDEX_LIST_NUMBER);
		return new Blocks(blockList.get()).getBlocks();
	}
	
	public static long getTotalTransactionNum() throws TronException{
		return new AdapterModel(WalletClient.getTotalTransaction().toByteArray()).getTransactionNum();
	}
	
}
