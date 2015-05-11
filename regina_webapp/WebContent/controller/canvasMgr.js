canvasMgr = function(){

	console.log("canvasMgr obj created ...");
	var currObj = this;
	this.currFloor = "";
	
	//Date attributes
	this.currMonth = ""; //01|02|03...
	this.currYear = "";
	this.currDay = "";
	this.dt = "";
	this.dayOfWeek = ""; //DOM|LUN|MAR...
	this.currMonthString = ""; //GEN|FEB|MAR...
	
	//Worker attributes
	this.lastDayOfMonth=0;
	this.daysLoaded=40;
	this.lastWorkerDay=1;
	this.enableWorker = true;
	this.daysArr = new Array();
	
	this.maintMode = false; // false, letti, stanze
	this.maintModeType = ""; // letti stanze
	this.modFlag = false;
	this.daysInMonthArray = new Array();
	this.yearList = new Array();
	this.currVis = "docs"; //docs,clean
	
	//Complete objects
	this.maintMgrBeds = new maintMgrBeds();
	this.maintMgrRooms = new maintMgrRooms();
    this.legendObj = new legendMgr();
	this.floorMgr = new floorMgr();
	this.doctorMgr = new doctorsMgr();
	this.cleaningMgr = new cleaningMgr();

	// Add 1 stage and 3 layers
	this.stage = new Kinetic.Stage({
		container : "container",
		width : 1164,
		height : 500,
		x : 10,
		draggable: false
	});
	//this.stage.setScale(0.9);
	this.floorLyr = new Kinetic.Layer({id: "floorLyr"});
	this.featureLyr = new Kinetic.Layer({id: "featureLyr"});
	this.doctorLyr = new Kinetic.Layer({id: "doctorLyr"});
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
		currObj.stage.add(currObj.doctorLyr);
		currObj.toolTipLyr.add(currObj.tooltip);
		currObj.stage.add(currObj.toolTipLyr);
		currObj.stage.add(currObj.occLyr);
		currObj.stage.add(currObj.featureLyr);
		
		// get all floor info from server
		currObj.getStaticInfo();
		currObj.createBuildingPulldown();
		currObj.createYearsPulldown();
		
		// initialize Selectors here if necessary.
		currObj.createDaysTable();
		currObj.selectorsChanged();
		//currObj.legendObj.populateStage(); // Old code with complete DoctorsList deprecated
		
		setTimeout(function() { canvasMgr.floorMgr.workerManager(); }, 1000);
		
	};

	// get floor list from server and load array...
	this.getStaticInfo = function() {
		
		var method = 'getStaticInfo';
		console.log('SRVR_call -> ' + method + " called ...");
		var params = {};
        try {
            
            $.ajax({
                url: "StaticInfoSrvlt",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	currObj.getStaticInfoElab(transport);
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
	
	this.getStaticInfoElab = function(transport) {
		
		if (transport.error == undefined) {
						
			currObj.floorArr = transport.ret2cli;
			currObj.yearList = transport.yearList;
			//currObj.currFloor="A0";
			
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
	this.createBuildingPulldown = function() {

		if ($('#building').val() == null) {
		
			// Just sort this bitch !!!
		    var sortedObj = new Array();
		    for (o in currObj.floorArr) {
		    	sortedObj.push(currObj.floorArr[o].floor_id);
		    }
		    sortedObj.sort();
		    
			for (obj in sortedObj) {
				
				$('#building').append($("<option/>", {
					 value: currObj.floorArr[sortedObj[obj]].floor_id,
					 text: currObj.floorArr[sortedObj[obj]].description
				}));
				
			}
			$('#building').val(currObj.currFloor);
			currObj.currFloor=""; //trick the system !!
		};
	};
	
	// create year pulldown ...
	this.createYearsPulldown = function() {
		
		// Just sort this bitch !!!
	    var sortedObj = new Array();
	    for (o in currObj.yearList) {
	    	sortedObj.push(currObj.yearList[o]);
	    }
	    sortedObj.sort();
	    
		for (obj in sortedObj) {
			
			$('#year').append($("<option/>", {
				 value: sortedObj[obj],
				 text: sortedObj[obj]
			}));
			
		}
	};
	
	this.selectorsChanged = function() {

		// capture current selector values
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
		currObj.prepareWorkerDtArray();
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
		//$('#header_display').val(day).change();
		if (day.toString().length == 1) day="0"+day;
		if (month.toString().length == 1) month="0"+month;
		var dt = year+month+day;
		
		currObj.checkToResetWorker(month, year, buildId);
		
		// flag used to empty occupancy array
		if (this.currMonth != month || this.currYear != year) {
			
			currObj.floorArr[buildId].bedByDate=[];
			console.log(" occMapArray empty for floor -> " + buildId);
			this.daysInMonthArray = new Array();
			for (var i=1; i<=currObj.lastDayOfMonth; i++) {
				var d=i;
				if (i.toString().length == 1) d="0"+i;
				this.daysInMonthArray.push(this.currYear+""+this.currMonth+""+d);
			};
		}
		
		currObj.dt = dt;
		currObj.currMonth = month;
		currObj.decodeMonth();
		currObj.currYear = year;
		currObj.currDay = day;
		
		d = new Date(currObj.currYear, Number(currObj.currMonth)-1, currObj.currDay);
		
		currObj.decodeDay(d.getDay());
		//$('#dayOfWeek').html(currObj.dayOfWeek);
		$('#dayVerbose').html(d.toDateString());
		var s = currObj.dayOfWeek + '-';
		if (currObj.currDay.length == 1) s=s+'0';
		s=s+currObj.currDay+"-"+currObj.currMonthString+"</br>"+currObj.currYear;
		$('#header_display').val(s).change();
		$('#dayOfWeek').html(s);
		
		// If floor selector changes (or on first run)
		if (this.currFloor != buildId) {
			
			// Note: The occupation array gets emptied on every floor change. 
			// This is done to spare some memory on the client and to keep info from getting stale.
			if (this.currFloor != "") {
				currObj.floorArr[this.currFloor].bedByDate=[];
				currObj.floorArr[this.currFloor].cleanByDate=[];
				console.log(" occMapArray empty for floor -> " + this.currFloor);
			}
			
			this.currFloor=buildId;
			currObj.floorMgr.populateFloorLayer(currObj.currFloor);
			currObj.floorMgr.getFeatureInfo(currObj.currFloor);
			
		}
		
		currObj.floorMgr.getAndPaintFloorOccupancyInfo();
		currObj.chooseRoomInfoOverlay();
		//canvasMgr.occLyr.batchDraw();
		//currObj.toolTipLyr.moveToTop();
		//canvasMgr.featureLyr.moveToTop;
	    canvasMgr.stage.batchDraw();
	};
	
	this.changeSliderDay = function(obj) {
		
		console.log('obj -> ' + obj );
		$('#day').slider( "option", "value", obj.innerHTML);
		currObj.selectorsChanged();
	};
	
	this.decodeDay = function(dayNum) {
		
		switch(dayNum) {
			case 0: currObj.dayOfWeek = "Domenica"; break;
			case 1: currObj.dayOfWeek = "Lunedi"; break;
			case 2: currObj.dayOfWeek = "Martedi"; break;
			case 3: currObj.dayOfWeek = "Mercoledi"; break;
			case 4: currObj.dayOfWeek = "Giovedi"; break;
			case 5: currObj.dayOfWeek = "Venerdi"; break;
			case 6: currObj.dayOfWeek = "Sabato"; break;
			default: currObj.dayOfWeek = "---";
		}
		
	};
	
	this.decodeMonth = function() {
		
		switch(currObj.currMonth) {
			case '01': currObj.currMonthString = "Gennaio"; break;
			case '02': currObj.currMonthString = "Febbraio"; break;
			case '03': currObj.currMonthString = "Marzo"; break;
			case '04': currObj.currMonthString = "Aprile"; break;
			case '05': currObj.currMonthString = "Maggio"; break;
			case '06': currObj.currMonthString = "Giugno"; break;
			case '07': currObj.currMonthString = "Luglio"; break;
			case '08': currObj.currMonthString = "Agosto"; break;
			case '09': currObj.currMonthString = "Settembre"; break;
			case '10': currObj.currMonthString = "Ottobre"; break;
			case '11': currObj.currMonthString = "Novembre"; break;
			case '12': currObj.currMonthString = "Dicembre"; break;
			default: currObj.dayOfWeek = "---";
		}
		
	};
	
	this.checkToResetWorker = function(newMonth, newYear, newBuildId) {
		
		if (currObj.currMonth != newMonth ||
			currObj.currYear  != newYear  ||
			currObj.currFloor != newBuildId) {
			
			this.resetWorker();
		}
	};
	
	this.resetWorker = function() {
		
		console.log('resetWorker ... called');
		currObj.daysLoaded=0;
		currObj.lastWorkerDay=1;
		currObj.prepareWorkerDtArray();
		setTimeout(function() { canvasMgr.floorMgr.workerManager(); }, 1000);
		currObj.createDaysTable();
		console.log("checkToResetWorker -> yes: currObj.lastDayOfMonth -> " + currObj.lastDayOfMonth);
		
	};
	
	this.chooseRoomInfoOverlay = function() {
		
		switch (canvasMgr.currVis) {
		case "docs":
			currObj.doctorMgr.getAndPaintDoctorOccupancyInfo();

			break;
		case "clean":
			currObj.cleaningMgr.getAndPaintCleaningInfo();
			break;
		default:
			console.log("did nothing, what happened ??");
		}
		
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
			//currObj.getFloorList(currObj.createBuildingPulldown);
			
			// initialize Selectors here if necessary.
			$("#radio3").button( "disable" );
			$("#radio1").button( "disable" );
			$("#radio4").button( "enable" );
			$("#radio2").button( "enable" );
			
			currObj.occLyr.destroyChildren();
			currObj.doctorLyr.destroyChildren();
			
			// Room objects
			if (currObj.maintModeType == "stanze") {
				currObj.maintMgrRooms.createRoomMaintInfo();
				//currObj.occLyr.draw();
			
			// Bed objects
			} else {
				currObj.maintMgrBeds.createBedMaintInfo();
			}
			
			currObj.stage.batchDraw();
						
		} else if (val == "off" || val == "salva") {
			
			if (currObj.modFlag) {
				var r = confirm("Qualcosa è cambiato vuoi salvare ?");
				if (r == true) {
				    val = "salva";
				}
			}
			
			if (val == "salva") {
				if (currObj.maintModeType == "stanze") {
					currObj.maintMgrRooms.saveFloorMap4FloorPrepare();
				} else {
					currObj.maintMgrBeds.collectLayerData();
				}				
			}
			
			
			currObj.maintMode=false;
			currObj.maintModeType="";
			currObj.currFloor = "";
			currObj.floorLyr.removeChildren();
			currObj.occLyr.removeChildren();
			// get all floor info from server
			//currObj.getFloorList(currObj.createBuildingPulldown);
			
			// initialize Selectors here if necessary.
			$("#radio3").button( "enable" );
			$("#radio1").button( "enable" );
			$("#radio4").button( "disable" );
			$("#radio2").button( "disable" );
			
			// Redraw everything
			currObj.getStaticInfo();
			currObj.selectorsChanged();
			$("#maintDiag").dialog('close');
			
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
	
	this.toggleVis = function() {
		
		// Just clean everything
		//currObj.floorArr[this.currFloor].bedByDate=[];
		//currObj.floorArr[this.currFloor].cleanByDate=[];
		
		if (canvasMgr.currVis == "docs") {
			canvasMgr.currVis = "clean";
			$("#visualizza").html('Assegnazione Medici');
		} else {
			canvasMgr.currVis = "docs";
			$("#visualizza").html('Pulizia Giornaliera');
		}
		console.log("currVis -> " + canvasMgr.currVis);
		currObj.chooseRoomInfoOverlay();
		canvasMgr.stage.batchDraw();
		this.resetWorker();
	};
	
	
	// Days table manager (start)
	this.createDaysTable = function() {
	
		// 31 gg -> 1.32em	
		// 30 gg -> 1.36em
		// 29 gg -> 1.38em
	    // 28 gg -> 1.45em
		var newWidth = "1.45em";
		
		switch(currObj.lastDayOfMonth) {
		case 28:
			newWidth = "3.4%";//"1.45em";
			break;
		case 29:
			newWidth = "3.25%";//"1.38em";
			break;
		case 30:
			newWidth = "3.15%";//"1.36em";
			break;
		case 31:
			newWidth = "3.04%";//"1.32em";
			break;
		}  
		
		var html = null;
		try {
			var myejs = new EJS({
				url : './view/days.ejs'
			});
			obj = {days : currObj.lastDayOfMonth,
				   width: newWidth};
			html = myejs.render(obj);
		} catch (e) {
			if (e.description)
				e = e.description;
			alert('ex -> ' + e);
		}

		$('#daysTable').html(html);
		
	};
	
	this.colorDay = function(day) {
		
		var dayNum = Number(day);
		console.log('colorDay for -> ' + dayNum);
		
		$('#day'+dayNum).css('color', 'black');
		$('#day'+dayNum).css('background-color', 'white');
		$('#day'+dayNum).parent().css('background-color', 'white');
	};
	
	// Days table manager (end)
	
	// Date management (start)
	this.prepareWorkerDtArray = function() {
		
		var b="";
		currObj.daysArr = new Array();
		for (var i=1; i<=currObj.lastDayOfMonth; i++) {
			if (i.toString().length == 1) {
				b="0"+i.toString();
			} else {
				b=i.toString();
			}
			currObj.daysArr.push(b);
		}
		
		// Just shuffle everything !!
		currObj.shuffle(currObj.daysArr);
		
	};
	
	this.shuffle = function(o){ 
	    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
	    return o;
	};
	
};