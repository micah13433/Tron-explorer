package com.tron.explorer.controller;


import java.util.List;

import org.jsoup.helper.StringUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.tron.explorer.Constrain;
import com.tron.explorer.model.Asset;
import com.tron.explorer.model.Assets;
import com.tron.explorer.model.TronException;
import com.tron.explorer.service.AssetService;
import com.tron.explorer.util.PageUtil;

public class AssetController extends Controller {
		
	public void index() throws TronException {
		
		Long currPageIndex = PageUtil.getPage(getPara("page"));		
		Assets assets = CacheKit.get("assetList", "index");
		if(assets == null){
			setAttr("count",0);
			render("asset/index.html");
			return;
		}
		int [] pageIndexArray = PageUtil.getPageIndex(assets.getAssets(),currPageIndex);
		List<Asset> assetList = assets.getAssets().subList(pageIndexArray[0], pageIndexArray[1]);		
	
		setAttr("currpage",currPageIndex);
		setAttr("totalpage",assets.getCount()/Constrain.pageSize + 1);
		setAttr("assets",assetList);
		setAttr("count", assets.getCount());
		render("asset/index.html");
	}
	
	public void list() throws TronException{
		Assets assets = CacheKit.get("assetList", "index");
		if(assets == null){
			assets = new Assets();
		}
		setAttr("assets", assets.getAssets());
		renderJson();
	}
	
	public void count() throws TronException{
		renderText(String.valueOf(CacheKit.get("persistedList", "assetNum")));
	}
	
	public void detail() throws TronException {
		String name = getPara("name");
		Asset asset = AssetService.getAssetByName(name);
		if(StringUtil.isBlank(asset.getName())){
			setAttr("status", 0);
		}else{
			setAttr("status", 1);
			setAttr("transCount",asset.getTrans().size());
			setAttr("asset", asset);
		}			
		render("asset/detail.html");
	}
}
 