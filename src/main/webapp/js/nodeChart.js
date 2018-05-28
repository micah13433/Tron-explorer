$(function () {
    // Create the chart
	$.ajax({
		type: "get",
		dataType: 'json',
		cache:false,
		url: '/node/chart',
		success:function(data){
			if(data.length <= 0) {
	            return false;
	        }
	        var ohlc = [],          
	            volume = [],
	            pieData = [];
	            dataLength = data.length,
	            i = 0;
	        for (i; i < dataLength; i += 1) {
	        	var  innerArray = [];
	            ohlc.push({
	                name: data[i].country,
	                y: data[i].count,
	                drilldown: data[i].country
	            });
	            pieData.push(
	            		 [data[i].country,   data[i].count]	
	            );
	            var innerDataMap = data[i].city;
	            for(var key in innerDataMap){
	            	
	        	innerArray.push([
	                             key,
	                             innerDataMap[key]
	                         ])
	        	}
	            volume.push({
	                name: data[i].country,
	                id: data[i].country,
	                data: innerArray
	            });
	        }
	        // create the chart
	        Highcharts.chart('container1', {
	            chart: {
	                type: 'column'
	            },
	            title: {
	                text: ''
	            },
	            xAxis: {
	                type: 'category'
	            },
	            yAxis: {
	                title: {
	                    text: ''
	                }
	            },
	            legend: {
	                enabled: false
	            },
	            plotOptions: {
	                series: {
	                    borderWidth: 0,
	                    dataLabels: {
	                        enabled: true                    }
	                }
	            },
	            tooltip: {
	                headerFormat: '',
	                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}</b><br/>'
	            },
	            series: [{
	                name: '',
	                colorByPoint: true,
	                data: ohlc
	            }],
	            drilldown: {
	                series: volume
	            }
	        });
	        
	        $('#container2').highcharts({
	            chart: {
	                plotBackgroundColor: null,
	                plotBorderWidth: null,
	                plotShadow: false
	            },
	            title: {
	                text: ''
	            },
	            tooltip: {
	                headerFormat: '',
	                pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
	            },
	            plotOptions: {
	                pie: {
	                    allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: true,
	                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                        style: {
	                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                        }
	                    }
	                }
	            },
	            series: [{
	                type: 'pie',
	                name: '浏览器访问量占比',
	                data: pieData
	            }]
	        });
		}
	});	
    
});

