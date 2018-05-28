$(document).ready(function(){
	//flushLatestBlocks();
	flushAssetNum();
	flushAccountNum();
	flushWitnessNum();
	flushTransActionNum();
	flushNodeNum();
}); 
function flushLatestBlocks(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/block/latest',
		success:function(res){
			var blockList = eval(res).blocks;
			var i=0;
			$("#loading").css("display","none");
			for(var i in blockList){
				$("#block_" + i).css("display","");
				$("#block_" + i + "_height").html(blockList[i].height);
				$("#block_" + i + "_generatorAddress").html(blockList[i].generatorAddress);
				$("#block_" + i + "_generatorAddress").attr("href","/block/detail?height=" + blockList[i].height);
				$("#block_" + i + "_generateTime").html(blockList[i].generateTime);
				$("#block_" + i + "_size").html(blockList[i].size + " Bytes");
				$("#block_" + i + "_numberOfTransactions").html(blockList[i].numberOfTransactions);
				i++;
			}
		}
	});	
}
function flushAssetNum(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/asset/count',
		success:function(res){
			$("#assetNum").html(res);
		}
	});	
}
function flushHeight(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/block/blockheight',
		success:function(res){
			$("#blockHeight").html(res)
		}
	});	
}
setInterval("flushHeight()",3000);
function flushAccountNum(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/account/count',
		success:function(res){
			$("#accountNum").html(res);
		}
	});	
}
setInterval("flushAccountNum()",5000);

function flushWitnessNum(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/delegate/count',
		success:function(res){
			$("#delegateNum").html(res);
		}
	});	
}
function flushTransActionNum(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/block/transactioncount',
		success:function(res){
			$("#transactionNum").html(res);
		}
	});	
}
setInterval("flushTransActionNum()",5000);

function flushNodeNum(){
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/node/count',
		success:function(res){
			if(res == 0) return;
			$("#nodeNum").html(res);
		}
	});	
}
setInterval("flushNodeNum()",5000);