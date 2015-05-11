cleaningMgr = function(){
	
	var cleaningMgrObj = this;
	this.dateArr = new Array();
	this.excelFiles = "";
	this.excelChosenFile = "";

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
			        opacity: canvasMgr.legendObj.cleanColor[cvalue].opacity,
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
	                $( "#diagRoom" ).html(locObj.loc_desc + " (CValue: " + cvalue +")");
	            });
	            poly2.on("mouseover", function(evt){
	            	console.log(evt.target);
	            	console.log(evt.target.getAttr('opacity'));
	            	evt.target.setAttr('opacity',1);
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
 	
 	/* Cleaning Form Dialog */
	this.showCleanDiag = function(loc_id, cval, autMan) {
	
		var desc = canvasMgr.floorArr[canvasMgr.currFloor].locArr[loc_id].room_num;
		var defVal = canvasMgr.floorArr[canvasMgr.currFloor].locArr[loc_id].cvalue;
		var vals = [0, 0.5, 1.00, 2.00, 3.00];
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
    
    
    // Excel Diag (START)
    this.getExcelFiles = function() {
 		
 	    var method = 'getExcelFiles';
 		console.log('SRVR_call -> ' + method + " called ...");
 		
         try {
         	
         	var params = {
         	    action: "getExcelFileList"
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
                 	cleaningMgrObj.getExcelFilesElab(transport);
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
 	
 	this.getExcelFilesElab = function(transport) {
 	
 		var method="getExcelFilesElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			cleaningMgrObj.excelFiles = transport.ret2cli.fileMap;
 			cleaningMgrObj.openExcelDiag();
 			
 		} else {
 			var err = transport.error; 
 			if (err.errorCode == 1) {
 				canvasMgr.showError(transport.error.errorDesc);
 			    
 			} else {
 				canvasMgr.showError(" in method -> " + method);
 			};
 			
 		};
 	};
    
    this.openExcelDiag = function() {
    	
    	var method  = 'openExcelDiag';
    	console.log(method + "called");
    	
    	var myejs = new EJS({
			url : './view/excelDialog.ejs'
		});
    	
    	var aFname = new Array();
    	var aId = new Array();
    	for (f in cleaningMgrObj.excelFiles) {
    		aFname.push(cleaningMgrObj.excelFiles[f].fileName);
    		aId.push(cleaningMgrObj.excelFiles[f].id);
    	}
    	
    	obj = {files : aFname,
    	       ids : aId};
	    html = myejs.render(obj);
    	
		$('#excelDiag').html(html);
    	
	    $( "#datepicker" ).datepicker({
		      numberOfMonths: 1,
		      showButtonPanel: false,
			  weekHeader: "W",
			  stepMonths: 0,
			  maxDate: "-2",
			  minDate: "+2",
			  dateFormat: "yymmdd",
			  onSelect: function(dateText, inst) {
				
				if (jQuery.inArray(dateText, cleaningMgrObj.dateArr) == -1) {
					cleaningMgrObj.dateArr.push(dateText);
				} else {
					cleaningMgrObj.dateArr = jQuery.grep(cleaningMgrObj.dateArr, function(value) { return value != dateText; });
				}
				
				$("#modExcel").prop('disabled',cleaningMgrObj.dateArr.length==0);
				
			  },
			  beforeShowDay: function (date){

				     var e = $.datepicker.formatDate("yymmdd", date);
					 
					 if (jQuery.inArray(e, cleaningMgrObj.dateArr) != -1) {	 
					   return [true,"ui-state-highlight", "Giorno Selezionato"];
					 } else {
					   return [true, ""];//enable all other days
					 }
			  } 
	    });
	    
    	$('#excelDiag').dialog('open');
    };
    
    // Excel Diag (End)
    
    this.changeFileName = function(obj) {
    	
    	cleaningMgrObj.excelChosenFile = cleaningMgrObj.excelFiles[obj.value].fileUri;
    	
    	cleaningMgrObj.dateArr = new Array();
    	console.log('change -> ' + obj.value);
    	console.log('id -> ' + cleaningMgrObj.excelFiles[obj.value].id);
    	$( "#datepicker" ).datepicker("option","maxDate", cleaningMgrObj.excelFiles[obj.value].dtLast );
    	$( "#datepicker" ).datepicker("option","minDate", cleaningMgrObj.excelFiles[obj.value].dtFirst );
    	$( "#datepicker" ).datepicker("refresh");
    	$( "#modExcel" ).prop('disabled',true);
    	
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
          	    cleanByLocMap: canvasMgr.floorArr[canvasMgr.currFloor].cleanByDate[canvasMgr.dt],
          	    dtSet: cleaningMgrObj.dateArr,
          	    excelChosenFile: cleaningMgrObj.excelChosenFile
          	};
          	params = { request: JSON.stringify(params) };
              
              $.ajax({
                  url: "CleaningSrvlt",
                  dataType: "json",
                  timeout: 10000,
                  type: 'POST',
                  async: true,
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
          
          $('#excelDiag').dialog('close');
    	
    };

    this.generateExcelElab = function(transport) {
    	
    	var method="generateExcelElab";
 		console.log(method + " called ...");
 		
 		if (transport.error == undefined) {
 			
 			if (transport.message != undefined) {
 				var msg = "File: '"+transport.message+"' modificato con successo.";
 				canvasMgr.showMsg(msg);
 				
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