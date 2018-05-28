package com.tron.explorer.model;

import java.util.ArrayList;
import java.util.List;

import org.tron.api.GrpcAPI.AssetIssueList;
import org.tron.protos.Contract.AssetIssueContract;

import com.tron.explorer.Constrain;
import com.tron.explorer.encrypt.Base58;
import com.tron.explorer.util.DecodeUtil;
import com.tron.explorer.util.FormatUtil;
import com.tron.explorer.util.XssUtil;


public class Assets  {
	
	private boolean success;
	private List<Asset> assets;
	private long count;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public List<Asset> getAssets() {
		return assets;
	}
	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}
	
	public Assets(){
		assets = new ArrayList<Asset>();
	}
	
	public Assets(AssetIssueList assetIssueList) throws TronException{
		assets = new ArrayList<Asset>();
		count = assetIssueList.getAssetIssueCount();
		List<AssetIssueContract> list = assetIssueList.getAssetIssueList();
		Asset asset = null;
		float price = 0;
		for(AssetIssueContract currAsset : list){
			asset = new Asset();
			asset.setName(XssUtil.xssEncode(currAsset.getName().toStringUtf8()));
			asset.setDecodeName(DecodeUtil.bytesToHex(currAsset.getName().toByteArray()));
			asset.setOwnerAddress(Base58.encodeChecked(currAsset.getOwnerAddress().toByteArray()));
			asset.setTotalSupply(currAsset.getTotalSupply());
			asset.setStartTime(FormatUtil.formatTimeInMillis(currAsset.getStartTime()));
			asset.setEndTime(FormatUtil.formatTimeInMillis(currAsset.getEndTime()));
			asset.setStartTimeOfLong(currAsset.getStartTime());
			asset.setEndTimeOfLong(currAsset.getEndTime());
			price = (float)currAsset.getTrxNum()/Constrain.ONE_TRX;
			price = price/currAsset.getNum();
			price = (float)(Math.round(price*1000))/1000;
			asset.setPrice(price);
			assets.add(asset);
		}		

	}
	
}
