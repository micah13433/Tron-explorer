package com.tron.explorer.util;

import java.util.List;

import com.tron.explorer.Constrain;

public class PageUtil {

	public static <T> int[] getPageIndex(List<T> blocks,
			Long currPageIndex) {
		int fromindex = (int) ((currPageIndex -1) * Constrain.pageSize);
		fromindex = fromindex > blocks.size() ? 0 : fromindex;
		int toindex = fromindex + Constrain.pageSize;		
		toindex = toindex > blocks.size() ? blocks.size() : toindex;
		return new int[]{fromindex,toindex};
	}

	public static Long getPage(String page) {
		Long currPageIndex = 1L;
		if(!StringUtils.isEmpty(page) && StringUtils.isNumeric(page)){
			currPageIndex = Long.valueOf(page);
		}
		if(currPageIndex <= 0){
			currPageIndex=1L;
		}
		return currPageIndex;
	}
	
}
