canvasMgr = function(){

	var currObj = this;
	this.stage = new Kinetic.Stage({
		container : "container",
		width : 1164,
		height : 500
	});
	this.floorArr = new Array();
	this.layers = new Array();
	this.tt = "ttLyr";
    this.tooltip = new Kinetic.Text({
        text: "",
        fontFamily: "Calibri",
        fontSize: 12,
        padding: 5,
        textFill: "white",
        fill: "black",
        alpha: 0.75,
        visible: false
    });

	var mImgOcc = new Image();
	var fImgOcc = new Image();
	var mImgPre = new Image();
	var fImgPre = new Image();

	// start here
	this.init = function() {

		currObj.prepIcons();
		currObj.createLayerForTooltip();
		currObj.getFloorList(currObj.createLayersForFloors);
		currObj.selectorsChanged();
	};
	
	// prepare icon images
	this.prepIcons = function() {
	
		mImgOcc.src = "./images/male_occ_20.png";
		fImgOcc.src = "./images/female_occ_20.png";
		mImgPre.src = "./images/male_pre_20.png";
		fImgPre.src = "./images/female_pre_20.png";
	};
		
	// get floor list from server and load array...
	this.getFloorList = function(_callback) {
		
		var method = 'getFloorList';
        try {
            
            $.ajax({
                url: "FloorList",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                //data: params,
                context: document.body,
                success: function(transport){
                	currObj.getFloorListElab(transport, _callback);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert("error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown);
                }
            });
            
        } 
        catch (e) {
            alert("ajax call error:" + e);
            return;
        }
		
	};
	
	this.getFloorListElab = function(transport, _callback) {
		
		if (transport.error == undefined) {
			
			for(var i=0; i<transport.ret2cli.length; i++) {	
				
				var flr = transport.ret2cli[i];
				
				var floor = new floorMgr();
				floor.id = flr.id;
				floor.desc = flr.description;
				floor.imgSrc = flr.imgSrc;
				floor.floorMap = flr.floorMap;
				currObj.floorArr[flr.id] = floor;				
			}
			
			_callback(); //createLayersForFloors
			
		} else {
			alert("There has been an error. Please trace...");
		};
	};
	
	// create a single layer for every floor object in the floorArray
	this.createLayersForFloors = function() {
		
		var buildId=0;
		
		for (obj in currObj.floorArr) {
			
			buildId = currObj.floorArr[obj].id;
			lyrImg = new Kinetic.Layer({id: buildId});
			imageObj = new Image();
			
			imageObj.src = currObj.floorArr[obj].imgSrc;

				var image = new Kinetic.Image({
					x : 0,
					y : 20,
					image : imageObj,
					width : 1164,
					height : 500,
					name: currObj.floorArr[obj].desc
				});
				
				if (true){
					image.on("mousemove", function(){
		                var mousePos = currObj.stage.getMousePosition();
		                var txt = "x=" + mousePos.x + " y=" + mousePos.y;
		                currObj.tooltip.setPosition(mousePos.x + 5, mousePos.y + 5);
		                currObj.tooltip.setText(txt);
		                currObj.tooltip.show();
		                currObj.layers[currObj.tt].draw();
		            });
				}
	            
				lyrImg.add(image);
				currObj.layers[buildId] = lyrImg;
				currObj.stage.add(lyrImg);
		}

		currObj.createBuildingPulldown(buildId);
		currObj.layers[buildId].moveToTop();

	};
	
	// create a single layer for tool tip
	this.createLayerForTooltip = function() {
	
		ttLayer = new Kinetic.Layer();
		ttLayer.add(currObj.tooltip);
		currObj.layers[currObj.tt] = ttLayer;
		currObj.stage.add(ttLayer);
	};
	
	// create pulldown with list of floors ...
	this.createBuildingPulldown = function(buildId) {

		for (obj in currObj.floorArr) {
			
			$('#building').append($("<option/>", {
				 value: currObj.floorArr[obj].id,
				 text: currObj.floorArr[obj].desc
			}));
			
		}
		$('#building').val(buildId);
	};
	
	//change floor
	this.changeFloor = function(obj) {
		
		var floorId = obj.value;
		currObj.layers[floorId].moveToTop();
		
	};
	
	//The idea is to create a new layer with id:'<buidingId>_<yyyymmdd>'
	this.createOccLayer = function(buildingId, day) {
		
		var lyrId = buildingId + "_" + day;
		
		if (currObj.layers[lyrId] == undefined) {

			currObj.layers[lyrId] = new Kinetic.Layer({id: lyrId});
			
			var image;
			var xVal;
			var yVal;
			var zoomFact = 1.07;
			var obj;
			
			var fMap = currObj.floorArr[buildingId].floorMap;
			var oMap = currObj.floorArr[buildingId].occMap[day];
			
			for (mObj in fMap) {
			    
				cObj = currObj.floorArr[buildingId].getObj4Bed(oMap, fMap[mObj].bed);
				
			 	if (cObj.gender == "M") {
					obj = mImgOcc;
				} else {
					obj = fImgPre;
				};
			    
			    xVal = fMap[mObj].xVal;
			    yVal = fMap[mObj].yVal;
			    
				image = new Kinetic.Image({
						x : xVal*zoomFact,
						y : yVal*zoomFact,
						image : obj,
						width : 20,
						height : 20,
						id: cObj.bed
				});
				
	            image.on("mousemove", function(){
	                var mousePos = currObj.stage.getMousePosition();
	                var txt = "Letto: " + cObj.bed;
	                currObj.tooltip.setPosition(mousePos.x + 5, mousePos.y + 5);
	                currObj.tooltip.setText(txt);
	                currObj.tooltip.show();
	                currObj.layers[currObj.tt].draw();
	            });
	 
	            image.on("mouseout", function(){
	                currObj.tooltip.hide();
	                currObj.layers[currObj.tt].draw();
	            });
	    	    //tooltipLayer.listen(false);
				currObj.layers[lyrId].add(image);
				//image = undefined;
			};
		};
		//currObj.layers[lyrId].draw();
		currObj.stage.add(currObj.layers[lyrId]);
		currObj.showLayer4Day(buildingId, lyrId);
		
	};
	
	// get occupancy for specific date and load array...
	this.getFloorOcc4DateList = function(buildId, dt, _callback) {
		
		var method = 'getFloorOcc4DateList';
        try {
        	
        	var params = {
                    buildId: buildId,
                    dt: dt
                };
            
            $.ajax({
                url: "FloorOccupancy",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	currObj.getFloorOcc4DateListElab(transport, _callback);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert("error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown);
                }
            });
            
        } 
        catch (e) {
            alert("ajax call error:" + e);
            return;
        }
		
	};
	
	this.getFloorOcc4DateListElab = function(transport, _callback) {
		
		if (transport.error == undefined) {
			
			ret = transport.ret2cli;
			currObj.floorArr[ret.id].occMap[ret.dt] = ret.occMap;
			_callback(ret.id, ret.dt);
			
		} else {
			alert("There has been an error. Please trace...");
		};
	};
	
	this.selectorsChanged = function() {
		
		var buildId="";
		var year="";
		var month="";
		var day="";
	
		// capture current values
		buildId = $('#building').val();
		year = $('#year').val();
		month = $('#month').val();
		day = $('#day').val();
		$('#dayvalue').text(day);
		
		if (day.length == 1) day="0"+day;
		var dt = year+month+day;
		
		var lyrId = buildId + "_" + dt;
		
		if (currObj.layers[lyrId] == undefined) {
			currObj.getFloorOcc4DateList(buildId, dt, currObj.createOccLayer);
		} else {
			currObj.showLayer4Day(buildId, lyrId);
		}
	};	
	
	this.showLayer4Day = function(buildId, bday) {
		
		currObj.layers[buildId].moveToTop();
		currObj.layers[bday].moveToTop();
		currObj.layers[currObj.tt].moveToTop();
		
	};	
};