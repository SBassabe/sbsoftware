canvasMgr = function(){

	var currObj = this;
	this.currFloor = "";

	// we need 1 stage and 3 layers
	this.stage = new Kinetic.Stage({
		container : "container",
		width : 1164,
		height : 500
	});

	this.floorLyr = new Kinetic.Layer({id: "floorLyr"});
	this.toolTipLyr = new Kinetic.Layer({id: "toolTipLyr"});
	this.occLyr = new Kinetic.Layer({id: "occLyr"});
	
	// wd nedd a FloorArray a ToolTip and some icons
	this.floorArr = new Array();
	//this.layers = new Array();
	//this.tt = "ttLyr";
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
	var xImgLib = new Image();
	
	// start here
	this.init = function() {

		currObj.prepIcons();
		currObj.addAllLayersToStage();
		currObj.getFloorList(currObj.createBuildingPulldown);
		// initialize Selectors here if necessary.
		currObj.selectorsChanged();
	};
	
	this.prepIcons = function() {
	
		mImgOcc.src = "./images/male_occ_20.png";
		fImgOcc.src = "./images/female_occ_20.png";
		mImgPre.src = "./images/male_pre_20.png";
		fImgPre.src = "./images/female_pre_20.png";
		xImgLib.src = "./images/stanza_lib_20.png";
	};

	this.addAllLayersToStage = function() {
		
		currObj.stage.add(currObj.floorLyr);
		currObj.toolTipLyr.add(currObj.tooltip);
		currObj.stage.add(currObj.toolTipLyr);
		currObj.stage.add(currObj.occLyr);
		
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
            
        } catch (e) {
            alert("ajax call error:" + e);
            return;
        }
	};
	
	this.getFloorListElab = function(transport, _callback) {
		
		if (transport.error == undefined) {
			
			var firstFloor="";
			
			for(var i=0; i<transport.ret2cli.length; i++) {	
				
				var flr = transport.ret2cli[i];
				
				var floor = new floorMgr();
				floor.id = flr.id;
				floor.desc = flr.description;
				floor.imgSrc = flr.imgSrc;
				floor.floorMap = flr.floorMap;
				currObj.floorArr[flr.id] = floor;
				
				if (i==0) firstFloor=flr.id;
			}
			
			_callback(firstFloor); //currObj.createBuildingPulldown
			
		} else {
			alert("There has been an error. Please trace...");
		};
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
	
	//The idea is to populate the occLayer
	this.createOccLayer = function(buildingId, day) {
		
		currObj.occLyr.removeChildren();
		
		var fMap = currObj.floorArr[buildingId].floorMap;
		var oMap = currObj.floorArr[buildingId].occMap[day];
		
		for (mObj in fMap) {( function() {
		//for (mObj in fMap) {
		    
			var cObj = currObj.floorArr[buildingId].getObj4Bed(oMap, fMap[mObj].bed);
			var rom = fMap[mObj].room;
			var bed = cObj.bed; 
			var obj;
			var zoomFact = 1;
			
			if (cObj.status == 0) {
				obj = xImgLib;
				bed = bed + " (libero)";
			} else {
				if (cObj.status == 1) {
					bed = bed + " (occupato)";
					if (cObj.gender == "M") {
						obj = mImgOcc;
					} else {
						obj = fImgOcc;
					};
				} else {
					bed = bed + " (prenotato)";
					if (cObj.gender == "M") {
						obj = mImgPre;
					} else {
						obj = fImgPre;
					};					
				} 	
			};
		    
		    var xVal = fMap[mObj].xVal;
		    var yVal = fMap[mObj].yVal;
			var image = new Kinetic.Image({
					x : xVal*zoomFact,
					y : yVal*zoomFact,
					image : obj,
					width : 20,
					height : 20,
					draggable: true,
					bed: cObj.bed,
					codStanza: fMap[mObj].codStanza,
					building: fMap[mObj].building,
					room: fMap[mObj].room
			});
			
            image.on("dragstart", function() {
            	document.body.style.cursor = "pointer";
                //layer.draw();
            });
            image.on("dragmove", function() {
                document.body.style.cursor = "pointer";
            });
            image.on("mouseover", function(){
            	document.body.style.cursor = "pointer";
            });
            image.on("mouseout", function() {
                document.body.style.cursor = "default";              
                $( "#diagRoom" ).html('---');
                $( "#diagBed" ).html('---');
            });
			
            image.on("mousemove", function(){
                //var mousePos = currObj.stage.getMousePosition();
                //var txt = " X=" + mousePos.x + " Y=" + mousePos.y;
                //var txt = "Letto: " + cObj.bed +" Nome: " + cObj.name + " Cognome: " + cObj.surname;
                //txt = txt + " Stanza: " + rom;
                //currObj.tooltip.setPosition(mousePos.x, mousePos.y);
                //currObj.tooltip.setText(txt);
                //currObj.tooltip.show();
                //currObj.toolTipLyr.draw();
                //$( "#diagFloor" ).html(fMap[mObj].building);
                $( "#diagRoom" ).html(rom);
                $( "#diagBed" ).html(bed);
            });
 /*
            image.on("mouseout", function(){
                currObj.tooltip.hide();
                currObj.toolTipLyr.draw();
            });
*/
            currObj.occLyr.add(image);
            //currObj.occLyr.draw();
            //currObj.stage.draw();
		}());
	  }
	  currObj.occLyr.draw();
	  currObj.stage.draw();
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
			_callback(ret.id, ret.dt); //currObj.createOccLayer
			
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
		
		if (this.currFloor != buildId) {
			this.currFloor=buildId;
			currObj.populateFloorLayer(buildId);	
		}

		if (currObj.floorArr[buildId].occMap[dt] == undefined ) {
			currObj.getFloorOcc4DateList(buildId, dt, currObj.createOccLayer);
		} else {
			currObj.createOccLayer(buildId, dt);
		}
		
		currObj.toolTipLyr.moveToTop();
	};
	
	this.populateFloorLayer = function(buildId) {

		imageObj = new Image();
		imageObj.onload = function() {
			var image = new Kinetic.Image({
				x : 0,
				y : 20,
				image : imageObj,
				width : 1164,
				height : 500,
				name: currObj.floorArr[buildId].desc
			});
			$( "#diagFloor" ).html(currObj.floorArr[buildId].desc);
            $( "#diagRoom" ).html('---');
            $( "#diagBed" ).html('---');
            $( "#diagNumBeds" ).html(currObj.floorArr[buildId].floorMap.length);
			currObj.floorLyr.removeChildren();
			currObj.floorLyr.add(image);
			currObj.stage.draw();
		};
		imageObj.src = currObj.floorArr[buildId].imgSrc;
		/*
		image.on("mousemove", function(){
            var mousePos = currObj.stage.getMousePosition();
            var txt = "x=" + mousePos.x + " y=" + mousePos.y;
            currObj.tooltip.setPosition(mousePos.x - 50, mousePos.y - 50);
            currObj.tooltip.setText(txt);
            currObj.tooltip.show();
            currObj.toolTipLyr.draw();
        });
        image.on("mouseout", function(){
            currObj.tooltip.hide();
            currObj.toolTipLyr.draw();
        });
		*/
	};
	
	this.showLayer4Day = function(buildId, bday) {
		
		currObj.layers[buildId].moveToTop();
		currObj.layers[bday].moveToTop();
		currObj.layers[currObj.tt].moveToTop();
		
	};
	
	this.clickMe = function() {

		// I need: //CODSTAN ;	NUMSTANZA ;	CODLETTO ; IDSEDE ;	X ;	Y,
		var txt="";
		for (obj in currObj.occLyr.getChildren()) {

			
			posObj=currObj.occLyr.children[obj].getPosition();
			//txt= txt+"id:"+currObj.occLyr.children[obj]._id+" X="+posObj.x+" Y="+posObj.y+"\n";
			
			var X = posObj.x;
			var Y = posObj.y;
			var CODSTAN = currObj.occLyr.children[obj].getAttrs().codStanza;
			var NUMSTANZA = currObj.occLyr.children[obj].getAttrs().room;
			var CODLETTO = currObj.occLyr.children[obj].getAttrs().bed;
			var IDSEDE = currObj.occLyr.children[obj].getAttrs().building;
			txt = txt + CODSTAN +';'+ NUMSTANZA +';'+ CODLETTO +';'+ IDSEDE +';'+ X +';'+ Y + ',';
		};
		$('#area').val(txt);
	
	};
	
	this.showDiag = function() {
		$( "#dialog" ).dialog('open');
	};
};