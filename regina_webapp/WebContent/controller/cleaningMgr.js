cleaningMgr = function(){
	
	var cleaningMgrObj = this;

	// Get info from server (START)
    this.getCleaningSchedule4DateList = function() {
 		
 	    var method = 'getCleaningSchedule4DateList';
 		console.log('SRVR_call -> ' + method + " called ... for dt -> " + canvasMgr.dt );
 		
         try {
         	
         	var params = {
         	    floor: canvasMgr.currFloor, 
         	    date: canvasMgr.dt,
         	    action: "get"};
         	
         	params = { request: JSON.stringify(params) };
             
             $.ajax({
                 url: "CleaningSrvlt",
                 dataType: "json",
                 timeout: 10000,
                 type: 'POST',
                 async: false,
                 data: params,
                 context: document.body,
                 success: function(transport){
                 	cleaningMgrObj.getCleaningSchedule4DateListElab(transport);
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
 	
 	this.getCleaningSchedule4DateListElab = function(transport) {
 	
 		var method="getCleaningSchedule4DateListElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt] = transport.ret2cli;
 			
 		} else {
 			var err = transport.error; 
 			if (err.errorCode == 1) {
 				canvasMgr.showError(transport.error.errorDesc);
 			    
 			} else {
 				canvasMgr.showError(" in method -> " + method);
 			};
 			
 		};
 	};
 	// Get info from server (END)
 	
    // Merge info to server (START)
 	this.mergeCleaningSchedule = function(loc_id) {
 		
 	    var method = 'mergeCleaningSchedule';
 		console.log('SRVR_call -> ' + method + " called ...");
 		
         try {
         	
         	var params = {
         	    floor: canvasMgr.currFloor, 
         	    date: canvasMgr.dt,
         	    action: "save", //canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id]
         	    //cleanByLocMap: canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id]
         	    cleanByLoc: canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id]
         	};
         	params = { request: JSON.stringify(params) };
             
             $.ajax({
                 url: "CleaningSrvlt",
                 dataType: "json",
                 timeout: 10000,
                 type: 'POST',
                 async: false,
                 data: params,
                 context: document.body,
                 success: function(transport){
                 	cleaningMgrObj.mergeCleaningScheduleElab(transport);
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
 	
 	this.mergeCleaningScheduleElab = function(transport) {
 	
 		var method="mergeCleaningScheduleElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			//All good do nothing ... for now
 			
 		} else {
 			var err = transport.error; 
 			if (err.errorCode == 1) {
 				canvasMgr.showError(transport.error.errorDesc);
 			    
 			} else {
 				canvasMgr.showError(" in method -> " + method);
 			};
 			
 		};
 	};
    // Merge info to server (END)
 	
 	// Just a test (START)
    this.getAllRoomOcc4GivenDT = function() {
 		
 	    var method = 'getAllRoomOcc4GivenDT';
 		console.log('SRVR_call -> ' + method + " called ...");
 		
         try {
         	
         	var params = {
         	    floor: canvasMgr.currFloor, 
         	    date: canvasMgr.dt,
         	    action: "test"};
         	params = { request: JSON.stringify(params) };
             
             $.ajax({
                 url: "CleaningSrvlt",
                 dataType: "json",
                 timeout: 10000,
                 type: 'POST',
                 async: false,
                 data: params,
                 context: document.body,
                 success: function(transport){
                 	console.log('debug here');
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
 	
 	this.getAndPaintCleaningInfo = function() {
 		
 		// check the occDocMap for occupancy info for the given date ...
 		if ($.isEmptyObject(canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt])) {
 		//if (canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt] == undefined) {
 			cleaningMgrObj.getCleaningSchedule4DateList();
 		}
 		cleaningMgrObj.paintCleaningInfo();
 	};
 	
 	this.paintCleaningInfo = function() {
 		
 		console.log("paintCleaningInfo called ...");
 		//this.createVacantRoomList();
 		canvasMgr.doctorLyr.removeChildren();
 		
 		// Doctor info
 		//var occDoc=floorMgrObj.occDocMap[dt];
 		//var uniqueRoomArray = new Array();
 		//canvasMgr.floorArr[canvasMgr.currFloor].uniqueDocArr = new Array();
 		
 		for (f in canvasMgr.floorArr[canvasMgr.currFloor].locArr) {( function() {    
 			
	 			var locObj = canvasMgr.floorArr[canvasMgr.currFloor].locArr[f];
			    var polyPnts = locObj.poly_points;
	 			var cvalue = canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][f];
	 			
	 			console.log('cvalue1 -> ' + cvalue);
	 			
	 			if (cvalue == undefined) {
	 				//cvalue = Number(canvasMgr.floorArr[canvasMgr.currFloor].locArr[locObj.room_num]);
	 				cvalue = Number(locObj.cvalue);
	 				var childObj = {cvalue: cvalue, loc_id: locObj.loc_id};
	 				canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][f] = childObj;
	 				                
	 			} else {
	 				cvalue = Number(cvalue.cvalue);
	 			}
			    
	 			console.log('cvalue2 -> ' + cvalue);
				
	 			var poly2 = new Kinetic.Line({
			        points: polyPnts.split(","),
			        fill: canvasMgr.legendObj.cleanColor[cvalue].rgba,
			        opacity: 0.6,
			        strokeWidth: 0,
			        id: "loc_"+f,
			        rooms: locObj.room_num,
			        draggable: false,
			        closed: true
			    });
			        
				poly2.on("mouseout", function() {;
					document.body.style.cursor = "default"; 
	                $( "#diagRoom" ).html('---');
	            });
				poly2.on("mousemove", function(){
	                $( "#diagRoom" ).html(locObj.room_num + " (CValue: " + cvalue +")");
	            });
	            poly2.on("mouseover", function(){
	            	document.body.style.cursor = "pointer";
	            });
				poly2.on("dblclick", function(){
					cleaningMgrObj.showCleanDiag(locObj.loc_id, cvalue);
	            });
				canvasMgr.doctorLyr.add(poly2);
				
 			
 			}());
 		};
 		
 		canvasMgr.legendObj.populateToolTipWithCleaningLegend();
 		//canvasMgr.doctorLyr.batchDraw();
 		//canvasMgr.stage.batchDraw();
 	};
 	// doctor occupancy functions(END)
 	
 	/* This method finds calculates the RoomOccupancy based on the BedOccupancy. If at least one bed is occupied (status="2") the room is rendered occupied. */
 	/*
 	this.createVacantRoomList = function() {
 		
 		var cleanRoomArr = new Array();
 		var bedObj;
 		
 		for (o in canvasMgr.floorArr[canvasMgr.currFloor].bedArr) {
 			
 			bedObj = canvasMgr.floorArr[canvasMgr.currFloor].bedArr[o];
 			occObj = canvasMgr.floorArr[canvasMgr.currFloor].bedByDate[canvasMgr.dt][o];
 			
 			if (cleanRoomArr[bedObj.room_num] == undefined) {
 				
 				if (occObj == undefined) {
 					cleanRoomArr[bedObj.room_num] = 0;
 				} else {
 					cleanRoomArr[bedObj.room_num] = occObj.status == "2" ? canvasMgr.floorArr[canvasMgr.currFloor].locArr[canvasMgr.currFloor+'_'+bedObj.room_num].cvalue : "0";
 				}
 				 
 			} else {
 				if (occObj == undefined) {
 				} else {
 					if (Number(occObj.status) > Number(cleanRoomArr[bedObj.room_num])) {
 						
 						cleanRoomArr[bedObj.room_num] = occObj.status == "2" ? "1" : cleanRoomArr[bedObj.room_num];
 					}
 				}	
 			}
 		}
 		canvasMgr.floorArr[canvasMgr.currFloor].cleanRoomArr = cleanRoomArr;
 		
 	};
 	*/
 	/* Cleaning Form Dialog */
	this.showCleanDiag = function(loc_id, cval, autMan) {
	
		var desc = canvasMgr.floorArr[canvasMgr.currFloor].locArr[loc_id].room_num;
		var defVal = canvasMgr.floorArr[canvasMgr.currFloor].locArr[loc_id].cvalue;
		var vals = [0, 0.25, 0.5, 0.75, 1.00, 1.25, 1.50, 1.75, 2.00];
		var autMan = 'Automatica';
		
		if (cval != defVal) { 
			autMan = 'Manuale';
		}
		
		try {
			param = {loc_id: loc_id, cval: cval, desc: desc, vals: vals, autMan: autMan, defVal: defVal};
			var myejs = new EJS({
				url : './view/cleaningForm.ejs'
			});
			html = myejs.render(param);
		} catch (e) {
			if (e.description)
				e = e.description;
			console.log('ex : ' + e);
		}
		$('#cleaningForm').html(html);
		$('#cleaningForm').dialog('open');
	
    };
    
	this.getSelectedCVal = function() {
		
		var loc_id = $('#cleanSelection').attr('loc_id');
		var cval = $('#cleanSelection').val();
		console.log("loc_id -> " + loc_id + ", cval -> " + cval);
		
		$('#cleaningForm').dialog('close');
		
		var obj = canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id];
		if (obj == undefined) {
			newObj = {cvalue: cval,
					  loc_id: loc_id
			          };
			canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id] = newObj;
		} else {
			obj.cvalue = cval;
			canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id] = obj;
		}
		
		cleaningMgrObj.mergeCleaningSchedule(loc_id);
		cleaningMgrObj.getAndPaintCleaningInfo();
		canvasMgr.stage.batchDraw();
    };
    
	this.getAllCVal = function() {
		
		
		var obj = canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id];
		if (obj == undefined) {
			newObj = {cvalue: cval,
					  loc_id: loc_id
			          };
			canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id] = newObj;
		} else {
			obj.cvalue = cval;
			canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt][loc_id] = obj;
		}
		
		cleaningMgrObj.mergeCleaningSchedule();
		cleaningMgrObj.getAndPaintCleaningInfo();
		canvasMgr.stage.batchDraw();
    };
    
    
    // Excel Elab (START)
    this.generateExcel = function() {
    	
    	var method = 'generateExcel';
  		console.log('SRVR_call -> ' + method + " called ...");
  		
          try {
        	  
          	var params = {
          	    floor: canvasMgr.currFloor,	
          	    date: canvasMgr.dt,
          	    action: "excel",
          	    cleanByLocMap: canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt]
          	};
          	params = { request: JSON.stringify(params) };
              
              $.ajax({
                  url: "CleaningSrvlt",
                  dataType: "json",
                  timeout: 10000,
                  type: 'POST',
                  async: false,
                  data: params,
                  context: document.body,
                  success: function(transport){
                  	cleaningMgrObj.generateExcelElab(transport);
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

    this.generateExcelElab = function(transport) {
    	
    	var method="generateExcelElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			if (transport.message != undefined) {
 				canvasMgr.showMsg(transport.message);
 			} else {
 				canvasMgr.showMsg("File prodotto correttamente !");
 			}

 		} else {
 			var err = transport.error; 
 			if (err.errorCode == 1) {
 				canvasMgr.showError(transport.error.errorDesc);
 			    
 			} else {
 				canvasMgr.showError(" in method -> " + method);
 			};
 			
 		};
    	
    };
    // Excel Elab (END)
};