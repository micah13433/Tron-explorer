function initMap() {
	$.ajax({
		type: "get",
		dataType: 'json',
		cache:false,
		url: '/node/google',
		success:function(data){
			var locations = [];
			if(data.length > 0) {	        
	        	for (i=0; i < data.length; i += 1) {
	        		locations.push({
	        			lat:parseFloat(data[i].latitude),
	        			lng:parseFloat(data[i].longitude)
	        		});
	        	}
	        }
			 var map = new google.maps.Map(document.getElementById('map'), {
			      zoom: 2,
			      center: new google.maps.LatLng(0,0),
			      mapTypeId: 'terrain'
			    });
			   var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
			   var markers = locations.map(function(location, i) {
			      return new google.maps.Marker({
			        position: location,
			        label: labels[i % labels.length]
			      });
			    });
				var markerCluster = new MarkerClusterer(map, markers,
			    {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});

			}
		});
};
   

