package com.tron.explorer.controller;


import com.jfinal.core.Controller;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.BaseQuery;
import com.tron.explorer.model.Block;
import com.tron.explorer.model.Blocks;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.BlockService;
import com.tron.explorer.util.PageUtil;

public class BlockController extends Controller {
		
	public void index() throws TronException {		
		Long currPageIndex = PageUtil.getPage(getPara("page"));			
		Long totalBlockNum = BlockService.getLatestBlockNumber();
		BaseQuery baseQuery = new BaseQuery();
		baseQuery.setLimit(Constrain.pageSize);
		Long startIndex = totalBlockNum - currPageIndex*Constrain.pageSize;
		if(startIndex <= 0){
			baseQuery.setEndset(Constrain.pageSize - Math.abs(startIndex));
		}else{
			baseQuery.setEndset(startIndex + Constrain.pageSize);
		}
		baseQuery.setOffset(startIndex <= 0 ? 1:startIndex);
		setAttr("currpage",currPageIndex);
		setAttr("totalpage",totalBlockNum/Constrain.pageSize + 1);
		Blocks blocks = BlockService.queryBlocks(baseQuery);
		setAttr("blocks",blocks.getBlocks());
		setAttr("count",blocks.getCount());
		render("block/index.html");
	}
	
	public void detail() throws TronException {
		long height = getParaToLong("height");
		Block block = BlockService.getBlockByHeight(height,true);
		setAttr("block", block);
		setAttr("transactions",block.getTradeList());
		render("block/detail.html");
	}
	
	public void latest() throws TronException {
		renderJson("blocks",BlockService.queryLatestBlocks());
	}
	
	public void transactioncount() throws TronException {
		renderText(String.valueOf(BlockService.getTotalTransactionNum()));
	}
	
	public void blockheight() throws TronException {
		long maxBlockHeight = BlockService.getLatestBlockNumber();
		renderText(String.valueOf(maxBlockHeight));
	}
}
 