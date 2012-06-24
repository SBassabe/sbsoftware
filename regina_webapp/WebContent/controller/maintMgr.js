maintMgr = function() {
	
	var maintObj = this;
	this.polyArray = new Array();
	this.doctorMap = new Array();
	
	this.initTst = function(layer, docMap) {
		
        //var cPoly1 = "73,192,73,160,340,23,500,109,499,139,342,93";
        //var cPoly2 = "626,401,628,324,744,324,994,323,996,403,628,402";

		for (d in docMap) {
			 maintObj.polyArray.push(docMap[d].polyPoints);
		}
		
		maintObj.doctorMap = docMap.slice();
		
        //maintObj.polyArray.push(cPoly1);
        //maintObj.polyArray.push(cPoly2);
     
        maintObj.createPolyAnchors(layer);
	    maintObj.captureBeforeDraw(layer);
		
	};

	this.buildDoctorInfo = function(layer, arr) {
		
		for (o in arr) {
			maintObj.polyArray.push(arr[o]);	
		}
     
        maintObj.createPolyAnchors(layer);
	    maintObj.captureBeforeDraw(layer);
		
	};
	
	this.updateDottedLines = function(layer) {
		
		//console.log("updateDotted");
		var i=0;
		for (o in maintObj.doctorMap) {
    	// for (i=0; i<maintObj.polyArray.length; i++) {
			
			var docObj = maintObj.doctorMap[o];
	        var c = eval("layer.polyPnts" + i);
	        
	        //var polyLine = layer.get('#polyLine'+i)[0];
	        var polyLine = layer.get('#p_'+docObj.docId)[0];
	        
	        if (c == undefined || polyLine == undefined) return;
	        
	        polyLine.setPoints([
	          c.start.attrs.x, c.start.attrs.y, 
	          c.control1.attrs.x, c.control1.attrs.y, 
	          c.control2.attrs.x, c.control2.attrs.y,
	          c.control3.attrs.x, c.control3.attrs.y,
	          c.control4.attrs.x, c.control4.attrs.y,
	          c.end.attrs.x, c.end.attrs.y
	        ]);
	        i++;    
    	}
	};
	
	this.buildAnchor = function(layer, x, y) {
		
		console.log("buildAnchor");
		var anchor = new Kinetic.Circle({
	          x: parseInt(x),
	          y: parseInt(y),
	          radius: 6,
	          stroke: "#666",
	          fill: "#ddd",
	          strokeWidth: 2,
	          draggable: true
	        });

	        // add hover styling
	        anchor.on("mouseover", function() {
	          document.body.style.cursor = "pointer";
	          this.setStrokeWidth(4);
	          layer.draw();
	        });
	        anchor.on("mouseout", function() {
	          document.body.style.cursor = "default";
	          this.setStrokeWidth(2);
	          layer.draw();
	        });

	        layer.add(anchor);
	        return anchor;
	};
	
	this.createPolyAnchors = function(layer) {
		
		  console.log("createPolyAnchors");
	  	  var poly;
		  var i=0;
		  var objPriv;
		  
		  //for (o in maintObj.polyArray) {
		  for (o in maintObj.doctorMap) {
			  
			//var c = maintObj.polyArray[o].split(",");
			var docObj = maintObj.doctorMap[o];  
			var c = docObj.polyPoints.split(",");
			//var id = "polyLine"+i;
			var id = "p_" + docObj.docId;
			console.log("anchor id -> " + id);
			console.log("docObj.polyPoints -> " + docObj.polyPoints);
		        
			// crate polygon and add it to layer
			poly = new Kinetic.Polygon({
			        //points: c,
			        fill: docObj.color,
			        alpha: 0.2,
			        stroke: docObj.color,
			        strokeWidth: 0,
			        id: id,
			        docName: docObj.docName,
			        name: docObj.docId,
			        rooms: docObj.rooms
		        });
		        layer.add(poly);
		        
	        // create anchor objs and add the to layer.
		    objPriv = {
	   	          start: maintObj.buildAnchor(layer, c[0],c[1]),
		          control1: maintObj.buildAnchor(layer, c[2],c[3]),
		          control2: maintObj.buildAnchor(layer, c[4],c[5]),
		          control3: maintObj.buildAnchor(layer, c[6],c[7]),
		          control4: maintObj.buildAnchor(layer, c[8],c[9]),
		          end: maintObj.buildAnchor(layer, c[10],c[11])
		    };
			eval("layer.polyPnts"+i+" = objPriv");
			i++;
		 }
	};
	
	this.captureBeforeDraw = function(layer) {
		
		console.log("captureBeforeDraw");
        layer.beforeDraw(function() {
        	maintObj.updateDottedLines(layer);
          });
	};
	
	this.clearDoctorInfo = function(layer) {
		
		console.log("clearDoctorInfo");
		maintObj.polyArray = new Array();

	};
	
	// Maintenance mode ...
	this.collectLayerData = function() {

		// Create a return object ....
		// # [bed_map] CODSTAN ;NUMSTANZA ;CODLETTO ; IDSEDE ;X ;Y
		// # [floor_feat] TYPE; ROOM; X; Y
		// # [doctor] DOCID; POLYPOINTS
		console.log("collectLayerData called....");
		
		var retObj = new returnObj();
		var doc;
		var color;
		var txt;
		var type;
		var room;
		var x;
		var y;
		var posObj;
		var id;
		var preFix="p_";
		var polyArr;
		
		for (obj in canvasMgr.floorLyr.getChildren()) {
			
			var child = canvasMgr.floorLyr.children[obj];
			// Doctors polyPoints
			// [doctor] DOCID;COLOR;POLYPOINTS, 
			// [doctor] DOCID; COLOR; DOCNAME; ROOMS; POLYPOINTS
			id = child.getAttrs().id;
			if (id != undefined && id.substr(0, preFix.length) == preFix) {
				
				console.log(id);
				doc = child.getAttrs().name;
				color = child.getAttrs().fill;
				docname = child.getAttrs().docName;
				room = child.getAttrs().rooms;
				
				polyArr = new Array();
				polyArr.push(doc);
				polyArr.push(color);
				polyArr.push(docname);
				polyArr.push(room);
				
				for (p in child.getAttrs().points) {
					pnt = child.getAttrs().points[p];
					polyArr.push(pnt.x);
					polyArr.push(pnt.y);
				}
				
				txt=polyArr.join(";");
				retObj.doctorMap.push(txt);
				continue;
			}
			
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