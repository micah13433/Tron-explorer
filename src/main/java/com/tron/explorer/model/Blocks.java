package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tron.api.GrpcAPI.BlockList;


public class Blocks  {
	
	private boolean success;
	private List<Block> blocks;
	private long count;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	@SuppressWarnings("unchecked")
	public Blocks(BlockList blockList) throws TronException{
		blocks = new ArrayList<Block>();
		count = blockList.getBlockCount();
		List<org.tron.protos.Protocol.Block> list = blockList.getBlockList();
		for(int i=0; i< count; i++){
			if(list.get(i) == null) continue;
			blocks.add(new Block(list.get(i).toByteArray(),false));
		}
		Collections.sort(blocks, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Block j1 = (Block) o1;
				Block j2 = (Block) o2;
				int index = Long.valueOf(j2.getHeight()).compareTo(
						Long.valueOf(j1.getHeight()));
				return index;
			}
		});

	}
	
}
