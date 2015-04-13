maintMgrBeds = function() {
	
	var maintObj = this;
	this.polyArray = new Array();
	this.doctorMap = new Array();

	this.createBedMaintInfo = function() {
		
		console.log("createBedMaintInfo called ...");
		floorId = canvasMgr.currFloor;
		
		var fMap = canvasMgr.floorArr[floorId].bedArr;
		
		// populates with bed dots ...
		for (mObj in fMap) {( function() {
		    
			var rom = fMap[mObj].room_num;
			var bed = fMap[mObj].bed_num; 
			var obj;
			var zoomFact = 1;
		
			obj = canvasMgr.zImgLib;
		    
		    var xVal = fMap[mObj].x_val;
		    var yVal = fMap[mObj].y_val;
			var image = new Kinetic.Image({
					x : xVal*zoomFact,
					y : yVal*zoomFact,
					image : obj,
					width : 20,
					height : 20,
					bed: bed,
					codStanza: fMap[mObj].room_num,
					building: floorId,
					room: fMap[mObj].room_num,
					draggable: true
			});
			
            image.on("mouseout", function() {
                document.body.style.cursor = "default";              
                $( "#diagRoom" ).html('---');
                $( "#diagBed" ).html('---');
            });
		
            image.on("mousemove", function(){
            document.body.style.cursor = "pointer";
                $( "#diagRoom" ).html(rom);
                $( "#diagBed" ).html(bed);
            });

            canvasMgr.occLyr.add(image);
		}());
	  }
	  //layer.draw();
	  //stage.draw();
	  canvasMgr.stage.batchDraw();
	};
	
	// Maintenance mode ...
	this.collectLayerData = function() {

		// Create a return object ....
		// # [bed_map] CODSTAN ;NUMSTANZA ;CODLETTO ; IDSEDE ;X ;Y
		// # [floor_feat] TYPE; ROOM; X; Y
		console.log("collectLayerData called....");
		
		var retObj = new returnObj();
		var txt;
		var type;
		var room;
		var x;
		var y;
		var posObj;
		
		// Feature info
		for (obj in canvasMgr.featureLyr.getChildren()) {
			
			try {
				var child = canvasMgr.featureLyr.children[obj];
				type = child.attrs.featType;
			
				if (type != undefined) {
					
					room = canvasMgr.featureLyr.children[obj].attrs.room;
					posObj=canvasMgr.featureLyr.children[obj].attrs;
					x = Math.round(posObj.x);
					y = Math.round(posObj.y);
					
					txt = type + ";" + room + ";" + x + ";" + y;
					retObj.featureMap.push(txt);
				}
			} catch(e) {console.log("error ->" + e.message);}
		}
		
		// BedMap info
		for (obj in canvasMgr.occLyr.getChildren()) {
			
			try {
				posObj=canvasMgr.occLyr.children[obj].attrs;
				//txt= txt+"id:"+currObj.occLyr.children[obj]._id+" X="+posObj.x+" Y="+posObj.y+"\n";
				// java sctructure -> buildId;codBed;codRoom;guiRoom;xVal;yVal;DB
				
				var X = Math.round(posObj.x);
				var Y = Math.round(posObj.y);
				var CODSTAN = canvasMgr.occLyr.children[obj].attrs.codStanza;
				var NUMSTANZA = canvasMgr.occLyr.children[obj].attrs.room;
				var CODLETTO = canvasMgr.occLyr.children[obj].attrs.bed;
				var IDSEDE = canvasMgr.occLyr.children[obj].attrs.building;
				//txt = CODSTAN +';'+ NUMSTANZA +';'+ CODLETTO +';'+ IDSEDE +';'+ X +';'+ Y;
				txt = IDSEDE +';'+ CODLETTO +';'+ CODSTAN +';'+ NUMSTANZA +';'+ X +';'+ Y;
				retObj.floorMap.push(txt);
			} catch(e) {console.log("error ->" + e.message);}
		};
		
		maintObj.callMgmtSrvlt(retObj);
	};
	
	this.callMgmtSrvlt = function(reqObj) {
		
		console.log('SRVR_call -> ' + "callMgmtSrvlt called ...");
        
		var param = {
		    currFloor: canvasMgr.currFloor,
			featureMap: reqObj.featureMap.join(","),
			floorMap: reqObj.floorMap.join(","), 
			doctorMap: reqObj.doctorMap.join(",")
		};	
		
		try {
         
            $.ajax({
                url: "MaintSrvlt",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: param,
                context: document.body,
                success: function(transport){
                	maintObj.callMgmtSrvltElab(transport);
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
	
	this.callMgmtSrvltDoc = function(reqObj, doc2del, act) {
		
		console.log('SRVR_call -> ' + "callMgmtSrvltNewDoc called ...");
 
		var param = {
			action: act,	
		    currFloor: canvasMgr.currFloor,
			doctorMap: reqObj,
			doc2del: doc2del
		};	
		
		try {
         
            $.ajax({
                url: "MaintSrvlt",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: param,
                context: document.body,
                success: function(transport){
                	maintObj.callMgmtSrvltElab(transport);
                },
                error: function(jqXHR, textStatus, errorThrown){
                	
                	canvasMgr.showError("error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown);
                }
            });
            
        } 
        catch (e) {
        	canvasMgr.showError("ajax call error:" + e);
            return;
        }
		
	};
	
	this.callMgmtSrvltElab = function(transport) {
		
		console.log("callMgmtSrvltElab called ...");
		
		if (transport != null && transport.error != "0") {
			
			console.log("all good");
			canvasMgr.showMsg("Dati salvati con successo ...");
			
		} else {
			canvasMgr.showError("There has been an error. Please trace...");
		};
	};
	
};