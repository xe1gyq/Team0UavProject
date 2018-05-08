function UAV() {
	//-------------------------------
	this.frameIcon1 = {
		path:"M-30.14012510194162,-0.6472990536338017 C-30.14012510194162,-4.36100347511945 -26.9396213477545,-7.369104056522829 -22.988382145054345,-7.369104056522829 C-19.037142942354194,-7.369104056522829 -15.836639188167071,-4.36100347511945 -15.836639188167071,-0.6472990536338017 C-15.836639188167071,3.06640536785185 -19.037142942354194,6.074505949255226 -22.988382145054345,6.074505949255226 C-26.9396213477545,6.074505949255226 -30.14012510194162,3.06640536785185 -30.14012510194162,-0.6472990536338017 z M-6.711553535002267,24.0669865286702 C-6.711553535002267,20.353282107184548 -3.511049780815142,17.34518152578117 0.44018942188500887,17.34518152578117 C4.39142862458516,17.34518152578117 7.5919323787722846,20.353282107184548 7.5919323787722846,24.0669865286702 C7.5919323787722846,27.78069095015585 4.39142862458516,30.788791531559227 0.44018942188500887,30.788791531559227 C-3.511049780815142,30.788791531559227 -6.711553535002267,27.78069095015585 -6.711553535002267,24.0669865286702 z M17.145587020975555,0.20984434846580768 C17.145587020975555,-3.503860073019837 20.346090775162672,-6.51196065442322 24.29732997786283,-6.51196065442322 C28.24856918056298,-6.51196065442322 31.449072934750106,-3.503860073019837 31.449072934750106,0.20984434846580768 C31.449072934750106,3.923548769951452 28.24856918056298,6.931649351354835 24.29732997786283,6.931649351354835 C20.346090775162672,6.931649351354835 17.145587020975555,3.923548769951452 17.145587020975555,0.20984434846580768 z M-7.140127858656442,-24.79015735026657 C-7.140127858656442,-28.50386177175222 -3.939624104469317,-31.511962353155596 0.011615098230834064,-31.511962353155596 C3.962854300930985,-31.511962353155596 7.16335805511811,-28.50386177175222 7.16335805511811,-24.79015735026657 C7.16335805511811,-21.076452928780917 3.962854300930985,-18.06835234737754 0.011615098230834064,-18.06835234737754 C-3.939624104469317,-18.06835234737754 -7.140127858656442,-21.076452928780917 -7.140127858656442,-24.79015735026657 z M-15.496242274669473,0.06630312002640437 L16.634312041951254,0.06630312002640437 M0.28571338525838996,-17.78571321283068 L0.42857052811552876,17.5000010728836 M-25.14285719394684,-0.5714285969734192 C-25.14285719394684,-1.676400972664027 -24.247829569637446,-2.571428596973419 -23.14285719394684,-2.571428596973419 C-22.03788481825623,-2.571428596973419 -21.14285719394684,-1.676400972664027 -21.14285719394684,-0.5714285969734192 C-21.14285719394684,0.5335437787171886 -22.03788481825623,1.4285714030265808 -23.14285719394684,1.4285714030265808 C-24.247829569637446,1.4285714030265808 -25.14285719394684,0.5335437787171886 -25.14285719394684,-0.5714285969734192 z M-1.9999997168779375,-24.85714226961136 C-1.9999997168779375,-25.962114645301966 -1.104972092568545,-26.85714226961136 2.8312206271086104e-7,-26.85714226961136 C1.1049726588126703,-26.85714226961136 2.0000002831220627,-25.962114645301966 2.0000002831220627,-24.85714226961136 C2.0000002831220627,-23.75216989392075 1.1049726588126703,-22.85714226961136 2.8312206271086104e-7,-22.85714226961136 C-1.104972092568545,-22.85714226961136 -1.9999997168779375,-23.75216989392075 -1.9999997168779375,-24.85714226961136 z M22.42857150733471,0.2857148051261902 C22.42857150733471,-0.8192575705644174 23.3235991316441,-1.7142851948738098 24.42857150733471,-1.7142851948738098 C25.533543883025317,-1.7142851948738098 26.42857150733471,-0.8192575705644174 26.42857150733471,0.2857148051261902 C26.42857150733471,1.3906871808167978 25.533543883025317,2.28571480512619 24.42857150733471,2.28571480512619 C23.3235991316441,2.28571480512619 22.42857150733471,1.3906871808167978 22.42857150733471,0.2857148051261902 z M-1.5714304745197296,24.142857044935226 C-1.5714304745197296,23.03788466924462 -0.6764028502103372,22.142857044935226 0.4285695254802704,22.142857044935226 C1.5335419011708782,22.142857044935226 2.4285695254802704,23.03788466924462 2.4285695254802704,24.142857044935226 C2.4285695254802704,25.247829420625834 1.5335419011708782,26.142857044935226 0.4285695254802704,26.142857044935226 C-0.6764028502103372,26.142857044935226 -1.5714304745197296,25.247829420625834 -1.5714304745197296,24.142857044935226 z",
		strokeColor: "#ffff00",
		strokeWeight: 3
	};	
	
	this.frameIcon2 = {
		path:"M-25.14285719394684,-0.5714285969734192 C-25.14285719394684,-1.676400972664027 -24.247829569637446,-2.571428596973419 -23.14285719394684,-2.571428596973419 C-22.03788481825623,-2.571428596973419 -21.14285719394684,-1.676400972664027 -21.14285719394684,-0.5714285969734192 C-21.14285719394684,0.5335437787171886 -22.03788481825623,1.4285714030265808 -23.14285719394684,1.4285714030265808 C-24.247829569637446,1.4285714030265808 -25.14285719394684,0.5335437787171886 -25.14285719394684,-0.5714285969734192 z M-1.9999997168779375,-24.85714226961136 C-1.9999997168779375,-25.962114645301966 -1.104972092568545,-26.85714226961136 2.8312206271086104e-7,-26.85714226961136 C1.1049726588126703,-26.85714226961136 2.0000002831220627,-25.962114645301966 2.0000002831220627,-24.85714226961136 C2.0000002831220627,-23.75216989392075 1.1049726588126703,-22.85714226961136 2.8312206271086104e-7,-22.85714226961136 C-1.104972092568545,-22.85714226961136 -1.9999997168779375,-23.75216989392075 -1.9999997168779375,-24.85714226961136 z",
		strokeColor: '#f15f5f',
		strokeWeight: 3
	};
	
	this.frameMarker1 = new google.maps.Marker({
		icon: this.frameIcon1,
		map: map.googlemap,
		optimized: false,
		zIndex: google.maps.Marker.MAX_ZINDEX + 1
	});
	
	this.frameMarker2 = new google.maps.Marker({
		icon: this.frameIcon2,
		map: map.googlemap,
		optimized: false,
		zIndex: google.maps.Marker.MAX_ZINDEX + 1
	});
	//---------------------------------------------
	this.drawUav = function() {
		try {
			this.frameIcon1.rotation = 45 + this.heading;
			this.frameMarker1.setIcon(this.frameIcon1);
			
			this.frameIcon2.rotation = 45 + this.heading;
			this.frameMarker2.setIcon(this.frameIcon2);
			
			this.frameMarker1.setPosition(this.currLocation);	
			this.frameMarker2.setPosition(this.currLocation);
		} catch(err) {
			console.log(">> [uav.drawUav()] " + err);
		}
	};	
	//----------------------------------------------
	this.currLocation = null;
	this.heading = 0;	
	this.setCurrLocation = function(latitude, longitude, heading) {
		try {
			this.currLocation = {lat:latitude, lng:longitude};
			this.heading = heading;
		} catch(err) {
			console.log(">> [uav.setCurrLocation()] " + err);
		}	
	};
	//---------------------------------------------
	this.headingLine = null;
	this.drawHeadingLine = function() {
		try {
			if(this.headingLine != null) {
				this.headingLine.setMap(null);
			}
			
			var endheadingLatLng = google.maps.geometry.spherical.computeOffset(
				new google.maps.LatLng(map.uav.currLocation), 
				2000, 
				this.heading
			);
			
			this.headingLine = new google.maps.Polyline({
				path: [
					this.currLocation,
					endheadingLatLng.toJSON()
				],
				strokeColor: '#FF0000',
				strokeOpacity: 1.0,
				strokeWeight: 2,
				map: map.googlemap
		    });
		} catch(err) {
			console.log(">> [uav.drawHeadingLine()] " + err);
		}
	};	
	//----------------------------------------------
	this.homeLocation = null;
	this.homeMarker = null;
	//---------------------------------------------
	this.goto = false;
	this.gotoMarker = null;
	this.gotoLocation = null;
	//---------------------------------------------
	this.mission = false;
	this.missionMarkers = [];
	this.missionPolylines = [];	
	this.nextLocation = null;	
	this.nextMarker = null;
	this.roiIndex = 0;
	//---------------------------------------------
	this.rtl = false;	
	//---------------------------------------------
	this.destLocation = null;
	this.destLine = null;	
	//---------------------------------------------
	this.drawDestLine = function() {
		try {
			if(this.goto == true) {
				this.destLocation = this.gotoMarker.getPosition().toJSON();
			} else if(this.mission == true) {
				this.destLocation = this.nextLocation;
			} else if(this.rtl == true) {
				this.destLocation = this.homeLocation;
			} else {
				this.destLocation = this.currLocation;
			}
			
			var bearing = google.maps.geometry.spherical.computeHeading(
				new google.maps.LatLng(this.currLocation), 
				new google.maps.LatLng(this.destLocation)
			);
			if(bearing<0) {
				bearing += 360;
			}
			
			var endbearingLatLng = google.maps.geometry.spherical.computeOffset(
				new google.maps.LatLng(this.currLocation), 
				2000, 
				bearing
			);
			
			if(this.destLine != null)  this.destLine.setMap(null);
			this.destLine = new google.maps.Polyline({
				path: [
					this.currLocation,
					endbearingLatLng.toJSON()
				],
				strokeColor: '#FF9900',
				strokeOpacity: 1.0,
				strokeWeight: 2,
				map: map.googlemap
		    });
		} catch(err) {
			console.log(">> [uav.drawDestLine()] " + err);
		}
	};		
	//---------------------------------------------
	this.setUavColor = function(color1, color2) {
		try {
			this.frameIcon1.strokeColor = color1;
			this.frameIcon2.strokeColor = color2;
			this.frameMarker1.setIcon(this.frameIcon1);
			this.frameMarker2.setIcon(this.frameIcon2);
		} catch(err) {
			console.log(">> [uav.setUavColor()] " + err);
		}
	};
	//---------------------------------------------
	this.setHomeLocation = function(latitude, longitude) {
		try {
			if(this.homeLocation == null || this.homeLocation.lat!=latitude || this.homeLocation.lng!=longitude) {
				this.homeLocation = {lat:latitude, lng:longitude};
				if(this.homeMarker != null) {
					this.homeMarker.setMap(null);
				}
				if(this.gotoMarker != null) {
					this.gotoMarker.setMap(null);
				}
				this.homeMarker = new google.maps.Marker({
					map: map.googlemap,
					position: this.homeLocation,
					label: {color:"#ffffff", text:"H"},
					optimized: false
				});

				map.googlemap.setCenter(map.uav.currLocation);
				map.googlemap.setZoom(18);
				jsproxy.setZoomSliderValue(18);
			}
		} catch(err) {
			console.log(">> [uav.setHomeLocation()] " + err);
		}
	};	
	//---------------------------------------------
	this.gotoStop = function() {
		try {
			this.goto = false;
			if(this.gotoMarker != null) {
				this.gotoMarker.setMap(null);
			}
		} catch(err) {
			console.log(">> [uav.gotoStart()] " + err);
		}
	};
	//---------------------------------------------
	this.makeMissionMark = function(kind, lat, lng, index) {
		try {
			var marker = null;
			
			if(kind == "takeoff") {
				marker = new google.maps.Marker({
					position: map.uav.homeLocation
				});
				marker.kind = kind;
				marker.index = 0;
				this.missionMarkers.splice(marker.index,0,marker);
			} 
			
			else if(kind == "waypoint") {
				marker = new google.maps.Marker({
					map: map.googlemap,
					position: {lat:lat, lng:lng},
					icon: {
					  path: google.maps.SymbolPath.CIRCLE,
					  fillOpacity: 1,
					  fillColor: '#fff',
					  strokeOpacity: 1,
					  strokeWeight: 1,
					  strokeColor: '#333',
					  scale: 12
					},
					draggable: true,
					optimized: false
				});
				marker.kind = kind;
				marker.index = this.missionMarkers.length;
				this.missionMarkers.push(marker);
				var self = this;
				marker.addListener('drag', function() {
					self.drawMissionPath();
				});
			} 
			
			else if(kind == "rtl") {
				marker = new google.maps.Marker({
					position: map.uav.homeLocation
				});
				marker.kind = kind;
				marker.index = this.missionMarkers.length;
				this.missionMarkers.push(marker);
			}
			
			else if(kind == "roi") {
				marker = new google.maps.Marker({
					map: map.googlemap,
					position: {lat:lat, lng:lng},
					draggable: true,
					optimized: false
				});
				marker.kind = kind;
				if(index) {
					marker.index = index;
					this.missionMarkers.splice(marker.index,0,marker);
				} else {
					marker.index = this.missionMarkers.length;
					this.missionMarkers.push(marker);
				}
			}			
			
			for(var i=0; i<this.missionMarkers.length; i++) {
				this.missionMarkers[i].index = i;
				if(this.missionMarkers[i].kind != "takeoff" && this.missionMarkers[i].kind != "rtl") {
					this.missionMarkers[i].setLabel({color: '#000', fontSize: '12px', fontWeight: '600', text: String(i+1)});
				}
			}
			
			this.drawMissionPath();
		} catch(err) {
			console.log(">> [uav.makeMissionMark()] " + err);
		}
	};
	//---------------------------------------------
	this.gotoStart = function(location) {
		try {
			this.goto = true;
			if(this.gotoMarker != null) {
				this.gotoMarker.setMap(null);
			} 
			this.gotoMarker = new google.maps.Marker({
				map: map.googlemap,
				position: location,
				optimized: false
			});
			this.missionStop();
			this.rtlStop();
			jsproxy.gotoStart(location);
		} catch(err) {
			console.log(">> [uav.gotoStart()] " + err);
		}
	};
	//---------------------------------------------
	this.missionStart = function() {
		this.mission = true;
		this.gotoStop();
		this.rtlStop();
	};
	//---------------------------------------------
	this.missionStop = function() {
		this.mission = false;
	};
	//---------------------------------------------
	this.rtlStart = function() {
		this.rtl = true;
		this.missionStop();
		this.gotoStop();
	};
	//---------------------------------------------
	this.rtlStop = function() {
		this.rtl = false;
	};	
	//---------------------------------------------
	this.removeMissionMark = function(index) {
		try {
			this.missionMarkers[index].setMap(null);
			this.missionMarkers.splice(index,1);
			for(var i=0; i<this.missionMarkers.length; i++) {
				this.missionMarkers[i].index = i;
				if(this.missionMarkers[i].kind != "takeoff" && this.missionMarkers[i].kind != "rtl") {
					this.missionMarkers[i].setLabel({color: '#000', fontSize: '12px', fontWeight: '600', text: String(i+1)});
				}
			}
			this.drawMissionPath();
		} catch(err) {
			console.log(">> [uav.gotoStart()] " + err);
		}
	};
	//---------------------------------------------
	this.clearMission = function() {
		try {
			for(var i=0; i<map.uav.missionMarkers.length; i++) {
				map.uav.missionMarkers[i].setMap(null);
			}
			map.uav.missionMarkers = [];
			for(var i=0; i<map.uav.missionPolylines.length; i++) {
				map.uav.missionPolylines[i].setMap(null);
			}
			map.uav.missionPolylines = [];	
		} catch(err) {
			console.log(">> [uav.clearMission()] " + err);
		}
	};
	//---------------------------------------------
	this.setMission = function(missionArr) {
		try {
			this.clearMission();
			for(var i=0; i<missionArr.length; i++) {
				this.makeMissionMark(missionArr[i].kind, missionArr[i].lat, missionArr[i].lng);
			}
		} catch(err) {
			console.log(">> [uav.setMission()] " + err);
		}
	};
	//---------------------------------------------
	this.setNextWaypointNo = function(nextWaypointNo) {
		try {
			if(this.mission == true) {
				if(1<=nextWaypointNo && (nextWaypointNo)<=this.missionMarkers.length) {
					this.nextLocation = this.missionMarkers[nextWaypointNo-1].getPosition().toJSON();
					this.nextMarker = this.missionMarkers[nextWaypointNo-1];
				}
			}
		} catch(err) {
			console.log(">> [uav.setNextWaypointNo()] " + err);
		}
	};
	//---------------------------------------------
	this.drawMissionPath = function() {
		try {
			for(var i=0; i<this.missionPolylines.length; i++) {
				this.missionPolylines[i].setMap(null);
			}
			this.missionPolylines = [];	
			
			var prevMarker = this.missionMarkers[0];
			var nextMarker = null;
			for(var i=1; i<this.missionMarkers.length; i++) {			 
				var polyline = new google.maps.Polyline({
					strokeColor: '#1ea4ff',
					strokeOpacity: 1.0,
					strokeWeight: 3,
					map: map.googlemap
				});
				
				nextMarker = this.missionMarkers[i];
				
				if(nextMarker.kind == "roi") {
					continue;
				} else {
					polyline.setPath([prevMarker.getPosition(), nextMarker.getPosition()]);
				}
				
				this.missionPolylines.push(polyline);
				prevMarker = nextMarker;
			}
		} catch(err) {
			console.log(">> [uav.drawMissionPath()] " + err);
		}
	};
	//Fence 관련 속성 및 메소드-----------------------------------------------------------
	this.fenceMarkers = [];
	this.makeFenceMark = function(lat, lng) {
		try {
			var image = {
	          url: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
	          size: new google.maps.Size(20, 32),
	          origin: new google.maps.Point(0, 0),
	          anchor: new google.maps.Point(0, 32)
	        };
			var marker = new google.maps.Marker({
				map: map.googlemap,
				position: {lat:lat, lng:lng},
				icon: image,
				draggable: true,
				optimized: false
			});
			var self = this;
			marker.addListener('drag', function() {
				self.drawFencePolygon();
			});
			this.fenceMarkers.push(marker);
			this.drawFencePolygon();
		} catch(err) {
			console.log(">> [uav.makeFenceMark()] " + err);
		}
	};
	this.setFence = function(fencePoints) {
		try {
			this.fenceClear();
			for(var i=0; i<fencePoints.length; i++) {
				this.makeFenceMark(fencePoints[i].lat, fencePoints[i].lng);
			}
		} catch(err) {
			console.log(">> [uav.setFence()] " + err);
		}
	};	
	this.removeFenceMark = function(index) {
		try {

		} catch(err) {
			console.log(">> [uav.removeFenceMark()] " + err);
		}
	};
	this.fenceClear = function() {
		try {
			if(this.fencePolygon != null) {
				this.fencePolygon.setMap(null);
			}
			for(var i=0; i<this.fenceMarkers.length; i++) {
				this.fenceMarkers[i].setMap(null);
			}
			this.fenceMarkers = [];
		} catch(err) {
			console.log(">> [uav.clearFence()] " + err);
		}
	};
	this.fencePolygon = null;
	this.drawFencePolygon = function() {
		try {
			var coords = [];
			for(var i=0; i<this.fenceMarkers.length; i++) {
				coords.push(this.fenceMarkers[i].getPosition());
			}
			if(this.fencePolygon != null) {
				this.fencePolygon.setMap(null);
			}
			this.fencePolygon = new google.maps.Polygon({
				map: map.googlemap,
				paths: coords,
				strokeColor: '#FF0000',
				strokeOpacity: 0.8,
				strokeWeight: 2,
				//fillColor: '#00ff00',
		        fillOpacity: 0.15
			});
			
			this.fencePolygon.addListener('click', function(e) {
				if(map.gotoMake == true) {
					var clickLocation = {lat:e.latLng.lat(), lng:e.latLng.lng()};
					map.uav.gotoStart(clickLocation);
				} else if(map.missionMake == true) {
					map.uav.makeMissionMark("waypoint", e.latLng.lat(), e.latLng.lng());
				} else if(map.roiMake == true) {
					map.uav.makeMissionMark("roi", e.latLng.lat(), e.latLng.lng(), map.uav.roiIndex);
					jsproxy.addROI({lat:e.latLng.lat(), lng:e.latLng.lng()});
					map.roiMake = false;
				}
			});
		} catch(err) {
			console.log(">> [uav.drawFencePolygon()] " + err);
		}
	};		
}






