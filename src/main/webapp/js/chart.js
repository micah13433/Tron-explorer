$(function () {
    Highcharts.setOptions({
        lang: {
            rangeSelectorZoom: ''
        }
    });
    $.ajax({
		type: "get",
		dataType: 'json',
		cache:false,
		url: '/market/chartData',
		success:function(data){
			var data = data.data;
	        if(data.code !== 0) {
	            return false;
	        }
	        var pricedata = data.data.price_usd;
	        var ohlc = [],
	            volume = [],
	            dataLength = pricedata.length,
	            volumnData = data.data.volume_usd,
	            // set the allowed units for data grouping
	            groupingUnits = [[
	                'day',                         // unit name
	                [1]                             // allowed multiples
	            ], [
	                'month',
	                [1, 2, 3, 4, 6]
	            ]],
	            i = 0;
	        for (i; i < dataLength; i += 1) {
	            ohlc.push([
	                pricedata[i][0], // the date
	                pricedata[i][1]
	            ]);
	            volume.push([
	                volumnData[i][0], // the date
	                volumnData[i][1] // the volume
	            ]);
	        }
	        // create the chart
	        $('#container').highcharts('StockChart', {
	            rangeSelector: {
	                selected: 1,
	                inputDateFormat: '%Y-%m-%d'
	            },
	            title: {
	                text: ''
	            },
	            xAxis: {
	                dateTimeLabelFormats: {
	                    millisecond: '%H:%M:%S.%L',
	                    second: '%H:%M:%S',
	                    minute: '%H:%M',
	                    hour: '%H:%M',
	                    day: '%m-%d',
	                    week: '%m-%d',
	                    month: '%y-%m',
	                    year: '%Y'
	                }
	            },
	            tooltip: {
	                split: false,
	                shared: true,
	            },
	            rangeSelector: {
	                buttons: [{
	                    type: 'day',
	                    count: 1,
	                    text: '1天'
	                }, {
	                    type: 'week',
	                    count: 1,
	                    text: '1周'
	                }, {
	                    type: 'month',
	                    count: 1,
	                    text: '1月'
	                }, {
	                    type: 'month',
	                    count: 6,
	                    text: '6月'
	                }, {
	                    type: 'year',
	                    count: 1,
	                    text: '1年'
	                }, {
	                    type: 'all',
	                    text: 'All'
	                }],
	                selected: 3
	            },
	            yAxis: [{
	                labels: {
	                    align: 'right',
	                    x: -3
	                },
	                title: {
	                    text: '股价'
	                },
	                height: '65%',
	                resize: {
	                    enabled: true
	                },
	                lineWidth: 2
	            }, {
	                labels: {
	                    align: 'right',
	                    x: -3
	                },
	                title: {
	                    text: '成交量'
	                },
	                top: '65%',
	                height: '35%',
	                offset: 0,
	                lineWidth: 2
	            }],
	            series: [{
	                type: 'line',
	                name: '价格(USD)',
	                color: 'green',
	                lineColor: 'green',
	                tooltip: {
	                },
	                navigatorOptions: {
	                    color: Highcharts.getOptions().colors[0]
	                },
	                data: ohlc,
	                dataGrouping: {
	                    units: groupingUnits
	                },
	                id: 'sz'
	            },{
	                type: 'column',
	                data: volume,
	                name:'24H成交量(USD)',
	                yAxis: 1,
	                dataGrouping: {
	                    units: groupingUnits
	                }
	            }]
	        });
		}
	});	
});
