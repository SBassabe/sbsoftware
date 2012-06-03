canvasMgr = function(){

	console.log("canvasMgr obj created ...");
	var currObj = this;
	this.currFloor = "";
	this.maintMode = true;
	this.maintMgr;
	
	if (currObj.maintMode) {
		currObj.maintMgr = new maintMgr();
	}

	// Add 1 stage and 3 layers
	this.stage = new Kinetic.Stage({
		container : "container",
		width : 1164,
		height : 500
	});
	this.floorLyr = new Kinetic.Layer({id: "floorLyr"});
	this.toolTipLyr = new Kinetic.Layer({id: "toolTipLyr"});
	this.occLyr = new Kinetic.Layer({id: "occLyr"});
	
	// Add a FloorArray a ToolTip and some icons
	this.floorArr = new Array();
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

	this.mImgOcc = new Image();
	this.fImgOcc = new Image();
	this.mImgPre = new Image();
	this.fImgPre = new Image();
	this.xImgLib = new Image();
	this.featImgI = new Image();
	this.featImgA = new Image();
	this.featImgS = new Image();
	this.featImgT = new Image();
	this.featImgB = new Image();
	
	this.mImgOcc.src = "./images/male_occ_20.png";
	this.fImgOcc.src = "./images/female_occ_20.png";
	this.mImgPre.src = "./images/male_pre_20.png";
	this.fImgPre.src = "./images/female_pre_20.png";
	this.xImgLib.src = "./images/stanza_lib_20.png";
	this.featImgI.src ="./images/letter_I_blue.png";
	this.featImgA.src ="./images/letter_A_blue.png";
	this.featImgS.src ="./images/letter_S_blue.png";
	this.featImgT.src ="./images/letter_T_blue.png";
	this.featImgB.src ="./images/letter_B_blue.png";
	
	// start here
	this.init = function() {

		console.log("init() Called ... 	");
		
		// add all layers to stage
		currObj.stage.add(currObj.floorLyr);
		currObj.toolTipLyr.add(currObj.tooltip);
		currObj.stage.add(currObj.toolTipLyr);
		currObj.stage.add(currObj.occLyr);
		
		// get all floor info from server
		currObj.getFloorList(currObj.createBuildingPulldown);
		
		// initialize Selectors here if necessary.
		currObj.selectorsChanged();
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
		
		// This function loads the floorArray[] with floor objects ....
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
	
	this.selectorsChanged = function() {

		// capture current values
		var buildId="";
		var year="";
		var month="";
		var day="";

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
			//currObj.populateFloorLayer(buildId);
			currObj.floorArr[buildId].populateFloorLayer(currObj.stage, currObj.floorLyr, currObj.occLyr, dt);
		} else {
			
			// populateOccLayer
			if (currObj.floorArr[buildId].occMap[dt] == undefined ) {
				currObj.floorArr[buildId].getFloorOcc4DateList(currObj.stage, currObj.occLyr, dt, currObj.floorArr[buildId].createOccLayer);
			} else {
				currObj.floorArr[buildId].createOccLayer(currObj.stage, currObj.occLyr, dt);
			}			
		}

		currObj.toolTipLyr.moveToTop();
	};

	// Maintenance mode ...
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