maintMgrRooms = function() {
	
	var curObj = this;
	this.doctorMap = new Array();
	this.floorId="";

	this.createRoomMaintInfo = function() {
		
		canvasMgr.modFlag = false;
		console.log("initTst");
		floorId = canvasMgr.currFloor;
		// For testing purposes only
		//curObj.doctorMap = [{"floor":"A0","codStanza":"736","numStanza":"4","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]},{"floor":"A0","codStanza":"733","numStanza":"3","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]},{"floor":"A0","codStanza":"698","numStanza":"19","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]},{"floor":"A0","codStanza":"700","numStanza":"22","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]},{"floor":"A0","codStanza":"699","numStanza":"21","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]}];
		//curObj.doctorMap = [{"floor":"A0","codStanza":"736","numStanza":"4","polyPoints":[244,132,287,137,291,254,146,258,144,183,243,177]},{"floor":"A0","codStanza":"733","numStanza":"3","polyPoints":[254,132,287,137,291,254,146,258,144,183,243,197]}];
		
		//curObj.getFloorMap4FloorPrepare();
		
		/*
		for (o in curObj.doctorMap) {
			obj=curObj.doctorMap[o];
			curObj.createPoly(obj.numStanza);
			curObj.createAnchors(obj.numStanza);
		}
		*/
		
		var rMap = canvasMgr.floorArr[canvasMgr.currFloor].locArr;
		
		// populates with roomPolygons
		for (r in rMap){
			curObj.createPoly(rMap[r].loc_id);
			curObj.createAnchors(rMap[r].loc_id);
		}
			
	};

	this.createPoly = function(roomId) {
		
		console.log("createPoly");
		
		var poly = new Kinetic.Line({
			fill: '#00D2FF',
	        stroke: 'black',
	        strokeWidth: 1,
	        opacity: 0.4,
	        draggable: true,
	        roomId: roomId,
	        name: 'p_'+roomId,
	        closed: true
		});
		
		//var obj = curObj.findRoomObject(roomId);
		var obj = canvasMgr.floorArr[canvasMgr.currFloor].locArr[roomId];
		poly.setPoints(obj.poly_points.split(","));

		poly.on("mouseout", function(){
			$( "#diagRoom" ).html('---');
        });
		
		poly.on("mousemove", function(){
			//$( "#diagRoom" ).html(roomId);
			$( "#diagRoom" ).html(obj.loc_desc);
        });
		
		poly.on("dragstart", function(pl) {
			
			curObj.deleteAnchors(pl.target.attrs.roomId);
			canvasMgr.occLyr.draw();
		});

		poly.on("dragend", function(pl) {
			
			 canvasMgr.modFlag = true;
			 var roomId = pl.target.attrs.roomId;
			 var dX=Math.round(pl.target.getPosition().x);
			 var dY=Math.round(pl.target.getPosition().y);
			 var docArrNew= new Array();
			 //var obj = curObj.findRoomObject(roomId);
			 var obj2 = canvasMgr.floorArr[canvasMgr.currFloor].locArr[roomId];
			 docArrNew = canvasMgr.floorArr[canvasMgr.currFloor].locArr[roomId].poly_points.split(",");
				
			 c=0;
			 for (a in docArrNew) {

			 	if (c==0) {
			 	    docArrNew[a] = Math.round(Number(docArrNew[a])+dX);
			 	    c=1;
			 	} else {
			 		docArrNew[a] = Math.round(Number(docArrNew[a])+dY);
			 		c=0;
			 	}
			 }
			 /*
	    	 for (var i=0; i<obj2.poly_points.length;) {
		  		docArrNew.push(eval(obj2.poly_points[i]+dX));
		  		docArrNew.push(eval(obj2.poly_points[i+1]+dY));
		  	    i++;
		  	    i++;
		  	 };
		  	 */
	  		 obj2.poly_points = new Array();
	  		 obj2.poly_points = docArrNew.toString();
	  		 curObj.deletePoly(roomId);
	  	     curObj.createPoly(roomId);
	  		 //curObj.deleteAnchors(roomId); // -> this may not be needed ...
	  		 curObj.createAnchors(roomId);
	  		 canvasMgr.occLyr.draw();
		});
		canvasMgr.occLyr.add(poly);
		
	};
	
	this.createAnchors = function(roomId) {
		
		console.log("createAnchors");
		
		//var obj = curObj.findRoomObject(roomId);
		var obj2 = canvasMgr.floorArr[canvasMgr.currFloor].locArr[roomId];
		var roomArray = new Array();
		roomArray = obj2.poly_points.split(",");
		
		for (var p=0; p<roomArray.length;) {
			
			var xCoord=parseInt(roomArray[p]);
			var yCoord=parseInt(roomArray[p+1]);	
			var anchor = new Kinetic.Circle({
				name: 'c_'+roomId,
				roomId: roomId,
				draggable: true,
				x: xCoord,
	  	        y: yCoord,
	  	        radius: 4,
	  	        stroke: "#666",
	  	        fill: "#ddd",
	  	        strokeWidth: 1
			});
					
			anchor.on("dragend", function(pl) {
				
				canvasMgr.modFlag = true;
				var roomId = pl.target.attrs.roomId;
				var shapes = canvasMgr.occLyr.get('.c_'+roomId);
  	        	var docArrNew= new Array();
  	    		for (s in shapes) {
	  	  			obj = shapes[s];
	  	  			try {
	  	  			    docArrNew.push(Math.round(obj.attrs.x));
	  	  			    docArrNew.push(Math.round(obj.attrs.y));
	  	  			} catch (e) { console.log("exception");} 
  	  			};
  	  		    //var obj = curObj.findRoomObject(roomId);
  	  		    var obj3 = canvasMgr.floorArr[canvasMgr.currFloor].locArr[roomId];
  	  		    obj3.poly_points = new Array();
  	  		    obj3.poly_points = docArrNew.toString();
  	  			curObj.deletePoly(roomId);
  	  			curObj.createPoly(roomId);
  	  			curObj.deleteAnchors(roomId);
  	  			curObj.createAnchors(roomId);
  	  		    canvasMgr.occLyr.draw();
			});
			
			anchor.on("mousemove", function(){
				$( "#diagRoom" ).html('X:'+xCoord+', Y:'+yCoord);
	        });
			anchor.on("mouseout", function(){
				$( "#diagRoom" ).html('---');
	        });
			
			canvasMgr.occLyr.add(anchor);
			p++;
			p++;
		}
	};
	
	this.deleteAnchors = function(roomId) {
		
		console.log("deleteAnchors");
		
		// delete anchors by name '.c_'+roomId
		var shapes = canvasMgr.occLyr.find('.c_'+roomId);  
  		for (s in shapes) {
			obj = shapes[s];
			try {
				obj.destroy();
			} catch (e) { console.log("exception" + e); return; }  /*get out of here on first error*/ 
		};
		canvasMgr.occLyr.draw();
	};
	
	this.deletePoly = function(roomId) {
		
		// delete polygons by name '.p_'+roomId
		console.log("deletePoly");
		
		var shapes = canvasMgr.occLyr.find('.p_'+roomId);  
  		for (s in shapes) {
			obj = shapes[s];
			try {
				obj.destroy();
			} catch (e) { console.log("exception" + e); return; }  /*get out of here on first error*/ 
		};
		canvasMgr.occLyr.draw();
	};	
	
	// Helper function
	this.findRoomObject = function(roomId) {
		  console.log("findRoomObject");
		  for (o in curObj.doctorMap) {
			  if (curObj.doctorMap[o].numStanza == roomId) return curObj.doctorMap[o];  
		  }
	};
	
	// Ajax Calls
	this.getFloorMap4FloorPrepare = function() {
		
		console.log('SRVR_call -> ' + "getFloorMap4FloorPrepare called ...");
		var method = 'getFloorMap4FloorPrepare';
        try {
        	
        	var params = {floor: curObj.floorId, action: "get"};
            
            $.ajax({
                url: "DoctorInfoSrvlt",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	curObj.getFloorMap4FloorElab(transport);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    var errDesc = "error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown;
                    canvasMgr.showError(errDesc);
                }
            });
            
        } 
        catch (e) {
        	canvasMgr.showError(" on method:" + method);
            console.log(e);
            return;
        }
		
	};
	
	this.getFloorMap4FloorElab = function(transport) {
		
		console.log("getFloorOcc4DateListElab called ...");
		var method="getFloorMap4FloorElab";
		
		if (transport.error == undefined) {
			
			ret = transport.ret2cli;
			curObj.doctorMap = ret;
			
		} else {
			var err = transport.error; 
			if (err.errorCode == 1) {
				canvasMgr.showError(transport.error.errorDesc);
			    
			} else {
				canvasMgr.showError(" in method -> " + method);
			};
			
		};
	};
	
	this.saveFloorMap4FloorPrepare = function() {
		
		console.log('SRVR_call -> ' + "saveFloorMap4FloorPrepare called ...");
		var method = 'saveFloorMap4FloorPrepare';
        try {
        	
        	var params = {floor: canvasMgr.currFloor, action: "save", locArr: canvasMgr.floorArr[canvasMgr.currFloor].locArr};
        	params = { request: JSON.stringify(params) };
        	
            $.ajax({
                url: "DoctorInfoSrvlt",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	curObj.saveFloorMap4FloorElab(transport);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    var errDesc = "error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown;
                    canvasMgr.showError(errDesc);
                }
            });
            
        } 
        catch (e) {
        	canvasMgr.showError(" on method:" + method);
            console.log(e);
            return;
        }
		
	};
	
	this.saveFloorMap4FloorElab = function(transport) {
		
		var method="getFloorMap4FloorElab";
		console.log(method + " called ...");
		
		if (transport != null && transport.error != "0") {
			
			console.log("all good");
			canvasMgr.showMsg("Dati salvati con successo ...");
			
		} else {
			canvasMgr.showError("There has been an error. Please trace...");
		};
	};
};