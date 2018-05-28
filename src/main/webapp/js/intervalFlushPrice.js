$(document).ready(function(){
	flushHead();
	flushMarket();
}); 
function flushHead(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: 'https://api.coinmarketcap.com/v1/ticker/tron/',
		success:function(res){
			if(res[0] && res[0].id){
				$("#tronprice").html(parseFloat(res[0].price_usd).toFixed(4));
				$("#tron1rate").html(res[0].percent_change_1h + " %");
				$("#tron24rate").html(res[0].percent_change_24h + " %");
				$("#tron24volume").html(parseInt(res[0]["24h_volume_usd"]/10000) + "w");
				$("#tronrate").html(res[0].rank);
			}
		}
	});	
}
setInterval("flushHead()",5000);
function flushMarket(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/market/priceList',
		success:function(res){
			if(res.markets && res.markets.length > 0){
				$.each(res.markets,function(index,item){
					ele = document.getElementById(item["marketKey"] + "_price");
					if(ele) {
						$(ele).html(item["price"]);
						$(document.getElementById(item["marketKey"] + "_volumn")).html(item["volumn"]);
						$(document.getElementById(item["marketKey"] + "_rate")).html(item["rate"]);
						$(document.getElementById(item["marketKey"] + "_fetchTime")).html(item["fetchTime"]);
					}
				});
			}
		}
	});
}