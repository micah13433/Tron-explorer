package com.tron.explorer.model;

public class BaseQuery {

	private int limit;
	private long offset;
	private long endset;
	private String order;
	private String sort;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public long getEndset() {
		return endset;
	}

	public void setEndset(long endset) {
		this.endset = endset;
	}
	
}
