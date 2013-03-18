maintMgrBeds = function() {
	
	var maintObj = this;
	this.polyArray = new Array();
	this.doctorMap = new Array();

	this.createOccLayerMaintInfo = function(stage, layer, floorId) {
		
		console.log("createOccLayerMaintInfo called ...");
		
		var fMap = canvasMgr.floorArr[floorId].floorMap;
		
		// populates with bed dots ...
		for (mObj in fMap) {( function() {
		    
			var rom = fMap[mObj].room;
			var bed = fMap[mObj].bed; 
			var obj;
			var zoomFact = 1;
		
			obj = canvasMgr.zImgLib;
		    
		    var xVal = fMap[mObj].xVal;
		    var yVal = fMap[mObj].yVal;
			var image = new Kinetic.Image({
					x : xVal*zoomFact,
					y : yVal*zoomFact,
					image : obj,
					width : 20,
					height : 20,
					bed: bed,
					codStanza: fMap[mObj].codStanza,
					building: fMap[mObj].building,
					room: fMap[mObj].room,
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

            layer.add(image);
		}());
	  }
	  layer.draw();
	  stage.draw();
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
		
		for (obj in canvasMgr.floorLyr.getChildren()) {
			
			var child = canvasMgr.floorLyr.children[obj];
			
			// Feature info
			type = child.getAttrs().featType;
			if (type != undefined) {
				
				room = canvasMgr.floorLyr.children[obj].getAttrs().room;
				posObj=canvasMgr.floorLyr.children[obj].getPosition();
				x = posObj.x;
				y = posObj.y;
				
				txt = type + ";" + room + ";" + x + ";" + y;
				retObj.featureMap.push(txt);
			}
		}
		
		// BedMap info
		for (obj in canvasMgr.occLyr.getChildren()) {
			
			posObj=canvasMgr.occLyr.children[obj].getPosition();
			//txt= txt+"id:"+currObj.occLyr.children[obj]._id+" X="+posObj.x+" Y="+posObj.y+"\n";
			// java sctructure -> buildId;codBed;codRoom;guiRoom;xVal;yVal;DB
			
			var X = posObj.x;
			var Y = posObj.y;
			var CODSTAN = canvasMgr.occLyr.children[obj].getAttrs().codStanza;
			var NUMSTANZA = canvasMgr.occLyr.children[obj].getAttrs().room;
			var CODLETTO = canvasMgr.occLyr.children[obj].getAttrs().bed;
			var IDSEDE = canvasMgr.occLyr.children[obj].getAttrs().building;
			//txt = CODSTAN +';'+ NUMSTANZA +';'+ CODLETTO +';'+ IDSEDE +';'+ X +';'+ Y;
			txt = IDSEDE +';'+ CODLETTO +';'+ CODSTAN +';'+ NUMSTANZA +';'+ X +';'+ Y;
			retObj.floorMap.push(txt);
		};
		
		maintObj.callMgmtSrvlt(retObj);
	};
	
	this.callMgmtSrvlt = function(reqObj) {
		
		console.log("callMgmtSrvlt called ...");
        
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
		
		console.log("callMgmtSrvltNewDoc called ...");
 
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