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
	var featImgI = new Image();
	var featImgA = new Image();
	var featImgS = new Image();
	var featImgT = new Image();
	var featImgB = new Image();
	
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
		featImgI.src ="./images/letter_I_blue.png";
		featImgA.src ="./images/letter_A_blue.png";
		featImgS.src ="./images/letter_S_blue.png";
		featImgT.src ="./images/letter_T_blue.png";
		featImgB.src ="./images/letter_B_blue.png";
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
				floor.featMap = flr.featureMap;
				floor.doctorMap = flr.doctorMap;
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
		
		// flag used to capture current floor change
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

		// Floor Image
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

			// Doctor info
			currObj.getDoctorInfo(buildId);
			// Floor features
			currObj.getFeatureInfo(buildId);
		
		};
		imageObj.src = currObj.floorArr[buildId].imgSrc;
		currObj.stage.draw();

	};
	
	this.getDoctorInfo = function(buildId) {
		
		// Doctor info
		for (f in currObj.floorArr[buildId].doctorMap) {( function() {
			
			var dObj = currObj.floorArr[buildId].doctorMap[f];
			//var polyPnts = "[" + dObj.polyPoints + "]";
			var polyPnts = dObj.polyPoints.split(",");
 			
			var poly2 = new Kinetic.Polygon({
			        points: polyPnts,
			        fill: "#00D2F0",
			        alpha: 0.2,
			        stroke: "#00D2F0",
			        strokeWidth: 0
		    });
		        
			currObj.floorLyr.add(poly2);
			currObj.floorLyr.draw();
			currObj.stage.draw();
			
			}());
		};		
	};
	
	this.getFeatureInfo = function(buildId) {
		
		var i=1;
		for (f in currObj.floorArr[buildId].featMap) {( function() {
			    //i++;
				var fObj = currObj.floorArr[buildId].featMap[f];
				var obj;
				var rom = fObj.room;
				var romDesc = rom+"["+ fObj.featDesc + "]";
				var featType = fObj.featType;
				
				// I;Infermeria,A;Studio Medico,S;Soggiorno Cucinino,T;Vano Tecnico,B;Bagno"
				switch (featType)
				{
				  case "I":
					  obj=featImgI;
					  break;
				  case "A":
					  obj=featImgA;
					  break;
				  case "S":
					  obj=featImgS;
					  break;
				  case "T":
				      obj=featImgT;
				  	  break;
				  default:
				      obj=featImgB;
				};
				
				xVal = fObj.xVal*i;
				yVal = fObj.yVal*i;

				var image = new Kinetic.Image({
					x : xVal,
					y : yVal,
					image : obj,
					width : 20,
					height : 20,
					featType : featType,
					room : rom,
					draggable: true
				});
				
				image.on("dragstart", function() {
	            	document.body.style.cursor = "pointer";
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
	            });
	            image.on("mousemove", function(){
	                $( "#diagRoom" ).html(romDesc);
	            });
				
				currObj.floorLyr.add(image);
				currObj.floorLyr.draw();
				currObj.stage.draw();
			}());
		};

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
		
		txt = txt + "\n\n[FloorFeatures for '" + this.currFloor  + "' floor]" ;
		
		for (obj in currObj.floorLyr.getChildren()) {

			var type = currObj.floorLyr.children[obj].getAttrs().featType;
			if (type == undefined) continue;
			
			posObj=currObj.floorLyr.children[obj].getPosition();
			//txt= txt+"id:"+currObj.occLyr.children[obj]._id+" X="+posObj.x+" Y="+posObj.y+"\n";
			//B1.floor_feat="I;509;10;10,S;515;11;11,T;508;12;12,B;500;13;13"
			// type; room; x; y
			
			var type = currObj.floorLyr.children[obj].getAttrs().featType;
			var room = currObj.floorLyr.children[obj].getAttrs().room;	
			var X = posObj.x;
			var Y = posObj.y;
			txt = txt + type +';'+ room +';'+ X +';'+ Y + ',';
		};
		
		$('#area').val(txt);
	
	};
	
	this.showDiag = function() {
		$( "#dialog" ).dialog('open');
	};
};