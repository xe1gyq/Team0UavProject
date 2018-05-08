//자바와 자바스크립트간의 통신을 위한 객체
var jsproxy = {	
	//-------------------------------------------------------
	//MapViewController.java의 zoomSlider의 value를 변경했을 경우 지도를 확대/축소
	//MapViewController.java -> jsproxy.js
	//지도 확대/축소 설정
	//일반지도(zoom 범위: 0~22)
	//위성사진(zoom 범위: 0~19)
	setMapZoom: function(zoom) {
		try {
			map.googlemap.setZoom(zoom);
		} catch(err) {
			console.log(">> [jsproxy.setMapZoom()] " + err);
		}
	},
	//-------------------------------------------------------
	//지도 상에서 마우스휠로 확대/축소할 경우 MapViewController.java의 zoomSlider의 value 변경
	//jsproxy.js -> MapViewController.java
	setZoomSliderValue: function(zoom) {
		try {
			jsproxy.java.setZoomSliderValue(zoom);
		} catch(err) {
			console.log(">> [jsproxy.setZoomSliderValue()] " + err);
		}
	},			
	//-------------------------------------------------------
	//처음 이륙 위치
	//GPS가 Fix되어야 됨
	setHomeLocation: function(latitude, longitude) {
		try {
			map.uav.setHomeLocation(latitude, longitude);
		} catch(err) {
			console.log(">> [jsproxy.setHomeLocation()] " + err);
		}
	},
	//-------------------------------------------------------
	//UAV의 현재 위치를 설정
	//latitude: 위도
	//longitude: 경도
	//heading: UAV 머리 방향(북:0, 동:90, 남:180, 서:270)
	setUavLocation: function(latitude, longitude, heading) {
		try {
			map.uav.setCurrLocation(latitude, longitude, heading);
		} catch(err) {
			console.log(">> [jsproxy.setUavLocation()] " + err);
		}	
	},
	//-------------------------------------------------------
	gotoMake: function() {
		try {
			map.gotoMake = true;
			map.roiMake = false;
			map.missionMake = false;
			map.fenceMake = false;
		} catch(err) {
			console.log(">> [jsproxy.gotoMake()] " + err);
		}
	},

	gotoStart: function(location) {
		try {
			location = JSON.stringify(location);
			jsproxy.java.gotoStart(location);
			map.uav.missionStop();
		} catch(err) {
			console.log(">> [jsproxy.gotoStart()] " + err);
		}
	},
	
	gotoStop: function() {
		map.gotoMake = false;
		map.uav.gotoStop();
	},
	
	//복귀
	//location: 목표지점 좌표
	rtlStart: function() {
		try {
			map.uav.rtlStart();
		} catch(err) {
			console.log(">> [jsproxy.rtl()] " + err);
		}
	},
	//-------------------------------------------------------
	missionMake: function() {
		try {
			map.gotoMake = false;
			map.missionMake = true;			
			map.roiMake = false;
			map.fenceMake = false;
		} catch(err) {
			console.log(">> [jsproxy.missionMake()] " + err);
		}	
	},
	//-------------------------------------------------------
	getMission: function() {
		try {
			var mission = [];
			for(var i=0; i<map.uav.missionMarkers.length; i++) {
				var json = {
					no: map.uav.missionMarkers[i].index+1,
					kind: map.uav.missionMarkers[i].kind,
					lat: map.uav.missionMarkers[i].getPosition().lat(),
					lng: map.uav.missionMarkers[i].getPosition().lng()
				};
				mission.push(json);
			}
			mission = JSON.stringify(mission);
			jsproxy.java.getMissionResponse(mission);
		} catch(err) {
			console.log(">> [jsproxy.getMission()] " + err);
		}
	},
	//-------------------------------------------------------
	setMission: function(strMessionArr) {
		try {
			var missionArr = JSON.parse(strMessionArr);
			map.uav.setMission(missionArr);
		} catch(err) {
			console.log(">> [jsproxy.setMission()] " + err);
		}
	},
	//-------------------------------------------------------
	missionStart: function() {
		try {
			map.uav.missionStart();
		} catch(err) {
			console.log(">> [jsproxy.missionStart()] " + err);
		}
	},
	//-------------------------------------------------------
	missionStop: function() {
		try {
			map.uav.missionStop();
		} catch(err) {
			console.log(">> [jsproxy.missionStop()] " + err);
		}	
	},
	//-------------------------------------------------------
	setNextWaypointNo: function(nextWaypointNo) {
		try {
			map.uav.setNextWaypointNo(nextWaypointNo);
		} catch(err) {
			console.log(">> [jsproxy.setNextWaypointNo()] " + err);
		}
	},
	//-------------------------------------------------------
	roiMake: function(selectedIndex) {
		try {
			map.gotoMake = false;
			map.missionMake = false;
			map.roiMake = true;
			map.fenceMake = false;
			map.uav.roiIndex = selectedIndex+1;
		} catch(err) {
			console.log(">> [jsproxy.roiMake()] " + err);
		}
	},
	addROI: function(location) {
		try {
			location = JSON.stringify(location);
			jsproxy.java.addROI(location);
		} catch(err) {
			console.log(">> [jsproxy.addROI()] " + err);
		}
	},
	//-------------------------------------------------------
	addTakeoff: function() {
		try {
			map.uav.makeMissionMark("takeoff");
		} catch(err) {
			console.log(">> [jsproxy.addTakeoff()] " + err);
		}
	},
	//-------------------------------------------------------
	addRTL: function() {
		try {
			map.uav.makeMissionMark("rtl");
		} catch(err) {
			console.log(">> [jsproxy.addRTL()] " + err);
		}
	},
	//-------------------------------------------------------
	removeMarker: function(selectedIndex) {
		try {
			map.uav.removeMissionMark(selectedIndex);
		} catch(err) {
			console.log(">> [jsproxy.removeMarker()] " + err);
		}
	},
	//-------------------------------------------------------
	fenceMake: function() {
		try {
			map.gotoMake = false;
			map.missionMake = false;			
			map.roiMake = false;
			map.fenceMake = true;
		} catch(err) {
			console.log(">> [jsproxy.fenceMake()] " + err);
		}	
	},
	fenceUpload: function() {
		try {
			var points = [];
			
			//index 0 is return point
			points.push(map.uav.homeLocation);
			
			for(var i=0; i<map.uav.fenceMarkers.length; i++) {
				points.push({lat:map.uav.fenceMarkers[i].getPosition().lat(), lng:map.uav.fenceMarkers[i].getPosition().lng()});
			}
			
			//last point is 0 index point for polygon
			points.push({lat:map.uav.fenceMarkers[0].getPosition().lat(), lng:map.uav.fenceMarkers[0].getPosition().lng()});
			
			points = JSON.stringify(points);
			jsproxy.java.fenceUpload(points);
		} catch(err) {
			console.log(">> [jsproxy.fenceUpload()] " + err);
		}
	},
	fenceClear: function() {
		map.uav.fenceClear();
	},
	//-------------------------------------------------------
	setFence: function(strFenceArr) {
		try {
			var fenceArr = JSON.parse(strFenceArr);
			var fencePoints = [];
			for(var i=1; i<(fenceArr.length-1); i++) {
				fencePoints.push({lat:fenceArr[i].lat, lng:fenceArr[i].lng});
			}
			map.uav.setFence(fencePoints);
		} catch(err) {
			console.log(">> [jsproxy.setFence()] " + err);
		}
	}
};
