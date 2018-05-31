package com.tron.explorer.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.http.HttpClient;
import com.tron.explorer.model.News;
import com.tron.explorer.model.TronException;

public class NewsService extends BaseService {
	
	public static List<News> queryNews() throws TronException{
		client = new HttpClient();
		List<News> result = new ArrayList<News>();
		String html = client.get(
				"https://api.jinse.com/v4/live/list?limit=20&reading=false&flag=down");
		JSONObject root = JSON.parseObject(html);
		JSONArray list = root.getJSONArray("list");
		JSONObject innerObj = null;
		JSONObject tinyObj = null;
		News news = null;
		String datePre = "";
		JSONArray innerArray = null;
		for(int i=0;i< list.size();i++){
			innerObj = list.getJSONObject(i);
			datePre = innerObj.getString("date");
			innerArray = innerObj.getJSONArray("lives");
			for(int j=0;j< innerArray.size();j++){
				tinyObj = innerArray.getJSONObject(j);
				if(tinyObj == null) continue;
				news = new News();
				String content = tinyObj.getString("content");
				String title = content.substring(content.indexOf("【")+1,content.indexOf("】"));
				content = content.substring(content.indexOf("】")+1);
				news.setContent(content);
				news.setTitle(title);
				news.setTime(datePre + "  " + getTime(tinyObj.getLong("created_at")));
				result.add(news);
			}
			
		}
		return result;
	}

	private static String getTime(Long mss) {
		mss += 8 * 60 * 60 * 1000;
		String DateTimes = null;
        long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % ( 60 * 60)) /60;
        long seconds = mss % 60;

        DateTimes=String.format("%02d:", hours)+ String.format("%02d:", minutes) + String.format("%02d", seconds);
        String.format("%2d:", hours);
        return DateTimes;
	}
	
}
