doctorsMgr = function(){
	
	var doctorMgrObj = this;

    // doctor occupancy functions(START)
    this.getDoctorOcc4DateList = function() {
 		
 	    var method = 'getDoctorOcc4DateList';
 		console.log('SRVR_call -> ' + method + " called ...");
 		
         try {
         	
         	var params = {
         	    floor: canvasMgr.currFloor, 
         	    date: canvasMgr.dt,
         	    action: "occ"};
             
             $.ajax({
                 url: "DoctorInfoSrvlt",
                 dataType: "json",
                 timeout: 10000,
                 type: 'POST',
                 async: false,
                 data: params,
                 context: document.body,
                 success: function(transport){
                 	doctorMgrObj.getDoctorOcc4DateListElab(transport);
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
 	
 	this.getDoctorOcc4DateListElab = function(transport) {
 	
 		var method="getDoctorOcc4DateListElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			canvasMgr.floorArr[canvasMgr.currFloor].docByDate[canvasMgr.dt] = transport.ret2cli;
 			
 		} else {
 			var err = transport.error; 
 			if (err.errorCode == 1) {
 				canvasMgr.showError(transport.error.errorDesc);
 			    
 			} else {
 				canvasMgr.showError(" in method -> " + method);
 			};
 			
 		};
 	};
 	
 	this.getAndPaintDoctorOccupancyInfo = function() {
 		
 		// check the occDocMap for occupancy info for the given date ...
 		if (canvasMgr.floorArr[canvasMgr.currFloor].docByDate[canvasMgr.dt] == undefined) {
 			doctorMgrObj.getDoctorOcc4DateList();
 		}
 		doctorMgrObj.paintDoctorOccupancyInfo();
 	};
 	
 	this.paintDoctorOccupancyInfo = function() {
 		
 		console.log("getDoctorInfo called ...");
 		canvasMgr.doctorLyr.removeChildren();
 		
 		// Doctor info
 		//var occDoc=floorMgrObj.occDocMap[dt];
 		var uniqueRoomArray = new Array();
 		canvasMgr.floorArr[canvasMgr.currFloor].uniqueDocArr = new Array();
 		
 		for (f in canvasMgr.floorArr[canvasMgr.currFloor].docByDate[canvasMgr.dt]) {( function() {    
 			
 			var dObj = canvasMgr.floorArr[canvasMgr.currFloor].docByDate[canvasMgr.dt][f];
 			var locObj = canvasMgr.floorArr[canvasMgr.currFloor].locArr[dObj.loc_id];
 			
	 			if (locObj != undefined) { 
	 			    // Capture distinct room numbers
	 				if ($.inArray(dObj.num_stanza, canvasMgr.floorArr[canvasMgr.currFloor].uniqueDocArr) > -1) {
	 					// Do next record ... pitty continue does not work
	 				} else {
	 					
	 				    // Capture distinct doctor list
	 				    if ($.inArray(dObj.doc_id, canvasMgr.floorArr[canvasMgr.currFloor].uniqueDocArr) < 0) {
	 				    	canvasMgr.floorArr[canvasMgr.currFloor].uniqueDocArr.push(dObj.doc_id);
	 				    }
	 					
	 					uniqueRoomArray.push(dObj.num_stanza);
	 				    var polyPnts = locObj.poly_points;
	 				
	 					if (canvasMgr.legendObj.docColors[dObj.doc_id] != undefined && polyPnts != undefined) {
	 		 			
	 						var poly2 = new Kinetic.Line({
	 					        points: polyPnts.split(","),
	 					        fill: canvasMgr.legendObj.docColors[dObj.doc_id].color,
	 					        opacity: 0.6,
	 					        strokeWidth: 0,
	 					        id: "p_" + dObj.doc_id,
	 					        docName: dObj.doc_name,
	 					        rooms: "rooms_dont_know",
	 					        draggable: false,
	 					        closed: true
	 					    });
	 					        
	 						poly2.on("mouseout", function() {;
	 			                $( "#diagRoom" ).html('---');
	 			            });
	 						poly2.on("mousemove", function(){
	 							var toDate=dObj.data_dal=="2030-01-01"?"---":dObj.data_al;	
	 			                $( "#diagRoom" ).html(dObj.num_stanza + " (Doc: " + dObj.doc_name + "[Dal: "+dObj.data_dal +" Al: "+toDate+"])");
	 			            });
	 					        
	 						canvasMgr.doctorLyr.add(poly2);
	 					}
	 				}
	 			} else {
	 				console.log('ATTENTION dObj.loc_id not mapped - > ' + dObj.loc_id);
	 			}	
 			
 			}());
 		};
 		canvasMgr.legendObj.populateToolTipLyrWithUniqueDocs();
 		//canvasMgr.doctorLyr.batchDraw();
 		//canvasMgr.stage.draw();
 	};
 	// doctor occupancy functions(END)
};