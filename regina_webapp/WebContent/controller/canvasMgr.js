canvasMgr = function(){

	console.log("canvasMgr obj created ...");
	var currObj = this;
	this.currFloor = "";
	this.currMonth = "";
	this.currYear = "";
	this.lastDayOfMonth=0;
	this.maintMode = false; // false, letti, stanze
	this.maintModeType = "";
	this.maintMgrBeds = new maintMgrBeds();
	this.maintMgrRooms = new maintMgrRooms();
	this.daysInMonthArray = new Array();
	this.legendObj = new legendMgr();

	// Add 1 stage and 3 layers
	this.stage = new Kinetic.Stage({
		container : "container",
		width : 1164,
		height : 500,
		x : 10
	});
	//this.stage.setScale(0.9);
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
	this.mImgOccConf = new Image();
	this.fImgOccConf = new Image();
	this.mImgPre = new Image();
	this.fImgPre = new Image();
	this.xImgLib = new Image();
	this.zImgLib = new Image();
	this.featImgI = new Image();
	this.featImgA = new Image();
	this.featImgS = new Image();
	this.featImgT = new Image();
	this.featImgB = new Image();
	this.featImgD = new Image();
	
	this.mImgOcc.src = "./images/male_occ_20.png";
	this.fImgOcc.src = "./images/female_occ_20.png";
	this.mImgOccConf.src = "./images/male_occ_20_conf.png";
	this.fImgOccConf.src = "./images/female_occ_20_conf.png";
	this.mImgPre.src = "./images/male_pre_20.png";
	this.fImgPre.src = "./images/female_pre_20.png";
	this.xImgLib.src = "./images/stanza_lib_20.png";
	this.zImgLib.src = "./images/stanza_maint_20.png";
	this.featImgI.src ="./images/letter_I_blue.png";
	this.featImgA.src ="./images/letter_A_blue.png";
	this.featImgS.src ="./images/letter_S_blue.png";
	this.featImgT.src ="./images/letter_T_blue.png";
	this.featImgB.src ="./images/letter_B_blue.png";
	this.featImgD.src ="./images/tv.png";
	
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
		currObj.legendObj.populateStage();
	};

	// get floor list from server and load array...
	this.getFloorList = function(_callback) {
		
		var method = 'getFloorList';
		var params = {
			maint: currObj.maintMode	
		};
        try {
            
            $.ajax({
                url: "FloorList",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	currObj.getFloorListElab(transport, _callback);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    var errDesc = "error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown;
                    canvasMgr.showError(errDesc);
                }
            });
            
        } catch (e) {
        	canvasMgr.showError(" on method:" + method);
            console.log(e);
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
				
				$.each(flr.roomMap, function(index, value) {
					floor.roomMap[value.numStanza]=value.polyPoints.toString();
				});
				
				currObj.floorArr[flr.id] = floor;  // <-- Important piece of code!
				
				if (i==0) firstFloor=flr.id;
			}
			
			_callback(firstFloor); //currObj.createBuildingPulldown
			
		} else {
			var err = transport.error;
			if (err.errorCode == 1) {
				canvasMgr.showError(transport.error.errorDesc);
			    
			} else {
				canvasMgr.showError(" in method -> " + method);
			};
		};
	};
	
	// create pulldown with list of floors ...
	this.createBuildingPulldown = function(buildId) {

		if ($('#building').val() == null) {
		
			for (obj in currObj.floorArr) {
				
				$('#building').append($("<option/>", {
					 value: currObj.floorArr[obj].id,
					 text: currObj.floorArr[obj].desc
				}));
				
			}
			$('#building').val(buildId);
		};
	};
	
	this.selectorsChanged = function() {

		// capture current values
		var buildId="";
		var year="";
		var month="";
		var day="";
		
		buildId = $('#building').val();
		day = $('#day').slider( "option", "value");
		
		// On first run, day equals 0. Set it to the current date.
		if (day==0) {
			var curDt = new Date();
			day = curDt.getDate();
			$('#year').val(curDt.getFullYear().toString());
			$('#month').val((curDt.getMonth()+1).toString().length == 1 ? "0"+(curDt.getMonth()+1).toString() : (curDt.getMonth()+1).toString());
			$('#day').slider( "option", "value", day);
		}
		
		year = $('#year').val();
		month = $('#month').val();
		
		// Get the max days in month and set the slider accordingly
		var d = new Date(year,month,0);
		currObj.lastDayOfMonth = d.getDate();
		$('#day').slider("option","max",currObj.lastDayOfMonth);
		$('#day').slider("option","min",1);
		
		// This watch dog tells me if the slider has gone beyond the max days for the month
		if (parseInt(day) > parseInt(currObj.lastDayOfMonth)) {
			$('#day').slider( "option", "max", currObj.lastDayOfMonth);
			day = $('#day').slider( "option", "max");
		};

		// collect values
		day=day+"";
		$('#dayvalue').text(day);
		if (day.toString().length == 1) day="0"+day;
		if (month.toString().length == 1) month="0"+month;
		var dt = year+month+day;
		
		// flag used to empty occupancy array
		if (this.currMonth != month || this.currYear != year) {
			currObj.floorArr[buildId].occMap=[];
			this.currMonth = month;
			this.currYear = year;
			console.log(" occMapArray empty for floor -> " + buildId);
			this.daysInMonthArray = new Array();
			for (var i=1; i<=currObj.lastDayOfMonth; i++) {
				var d=i;
				if (i.toString().length == 1) d="0"+i;
				this.daysInMonthArray.push(this.currYear+""+this.currMonth+""+d);
			};
		}
		
		// If floor selector changes (or on first run)
		if (this.currFloor != buildId) {
			
			// Note: The occupation array gets emptied on every floor change. 
			// This is done to spare some memory on the client and to keep info from getting stale.
			if (this.currFloor != "") {
				currObj.floorArr[this.currFloor].occMap=[];
				console.log(" occMapArray empty for floor -> " + this.currFloor);
			}
			
			this.currFloor=buildId;
			currObj.floorArr[buildId].populateFloorLayer(currObj.stage, currObj.floorLyr, currObj.occLyr, dt);
		
		// If floor selector does NOT change, just get occupancy info	
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

		console.log("clickMeDeprecate called....");
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
		
		$('#areaDiag').val(txt);
	
	};
	
	this.showDiag = function() {
		$( "#dialog" ).dialog('open');
	};
	
	this.showMntDiag = function() {
		
		$("#maintDiag").dialog('open');
		if (currObj.maintMode) {
			$("#radio2").button("enable"); //off
			$("#radio4").button("enable"); //salva
		} else {
			$("#radio2").button("disable");
			$("#radio4").button("disable");
		}
	};

	this.showLgndDiag = function() {
		
		try {
			var myejs = new EJS({
				url : './view/legenda.ejs'
			});
			html = myejs.render();
		} catch (e) {
			if (e.description)
				e = e.description;
			console.log('ex : ' + e);
		}
		$('#lgndDiag').html(html);
		$('#lgndDiag').dialog('open');
		
	};
	
	this.checkMaintRadio = function(val) {
		
		console.log("checkMaintRadio -> " + val);
		//if ($("#radio1").attr('checked') != undefined) {
		if (val == "letti" || val == "stanze") {	
			
			currObj.maintMode=true;
			currObj.maintModeType=val;

			// get all floor info from server
			currObj.getFloorList(currObj.createBuildingPulldown);
			
			// initialize Selectors here if necessary.
			$("#radio3").button( "disable" );
			$("#radio1").button( "disable" );
			$("#radio4").button( "enable" );
			$("#radio2").button( "enable" );
			
			currObj.occLyr.removeChildren();
			
			if (currObj.maintModeType == "stanze") {
				currObj.maintMgrRooms.initTst(currObj.currFloor);
				currObj.occLyr.draw();
			} else {
				currObj.maintMgrBeds.createOccLayerMaintInfo(currObj.stage, currObj.occLyr, currObj.currFloor);
			}
						
		} else if (val == "off" || val == "salva") {
			
			if (val == "salva") {
				if (currObj.maintModeType == "stanze") {
					currObj.maintMgrRooms.saveFloorMap4FloorPrepare();
					return;
				} else {
					currObj.maintMgrBeds.collectLayerData();
					return;
				}				
			}
			
			currObj.maintMode=false;
			currObj.currFloor = "";
			currObj.floorLyr.removeChildren();
			currObj.occLyr.removeChildren();
			// get all floor info from server
			currObj.getFloorList(currObj.createBuildingPulldown);
			
			// initialize Selectors here if necessary.
			currObj.selectorsChanged();
			$("#radio3").button( "enable" );
			$("#radio1").button( "enable" );
			$("#radio4").button( "disable" );
			$("#radio2").button( "disable" );
			
		};
	};
	
	this.showError = function(errDesc) {
		
		$("#errMsg").html("Errore dell'applicativo \n" + errDesc);
		$("#errDiag").dialog('open');
	};
	
	this.showMsg = function(msgDesc) {
		
		$("#msgMsg").html(msgDesc);
		$("#msgDiag").dialog('open');
	};
	
};