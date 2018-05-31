package com.tron.explorer.service;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.tron.explorer.http.HttpClient;
import com.tron.explorer.model.Market;
import com.tron.explorer.model.Market4M;
import com.tron.explorer.model.TronException;

public class MarketService extends BaseService {
	
	public static List<Market> queryPrices() throws TronException{
		client = new HttpClient(12000,12000);
		String html = client.get(
				"https://coinmarketcap.com/zh/currencies/tron/#markets");
		Document doc = Jsoup.parse(html);
		Element content = doc.getElementById("markets-table");
		List<Market> list = new ArrayList<Market>();
		if(content == null) return list;
		Elements trs = content.select("tbody tr");
		int maxNumber = 20;
		int currIndex = 0;
		Market market = null;
		for(Element tr : trs){			
			currIndex++;
			if(currIndex > maxNumber){
				break;
			}
			market = new Market();
			market.setMarketKey(tr.select("td").get(1).text() + tr.select("td").get(2).text());
			market.setVolumn(tr.select("td").get(3).attr("data-sort"));
			market.setPrice(tr.select("td").get(4).attr("data-sort"));
			market.setRate(tr.select("td").get(5).text());
			list.add(market);
		}
		doc = null;
		return list;
	}

	public static String queryChartData() throws TronException {
		client = new HttpClient(12000,12000);
		String html = client.get(
				"https://block.cc/api/v1/marketKline/tron");		
		return html;
	}
	
	public static List<Market4M> queryPrice4Extension() throws TronException {
		String html = client.get(
				"https://api.jinse.com/v4/market/web/list?currency_type=trx&type=1&currency=CNY&_source=m");
		List<Market4M> result = new ArrayList<Market4M>();
		JSONArray array = JSONArray.parseArray(html);
		Market4M obj = null;
		String rate = null;
		for(int i=0;i< array.size();i++){
			obj = new Market4M();
			obj.setDomain(array.getJSONObject(i).getString("domain"));
			obj.setLogo(array.getJSONObject(i).getString("logo"));
			obj.setName(array.getJSONObject(i).getString("exchange_name"));
			obj.setPrice(array.getJSONObject(i).getString("last"));
			rate = array.getJSONObject(i).getString("degree");
			if(rate!= null && !rate.startsWith("-")){
				rate = ("+" + rate);
			}
			
			rate = rate + "%";
			obj.setRate(rate);
			result.add(obj);
		}
		return result;
	}
	
}
