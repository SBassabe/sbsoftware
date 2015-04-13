floorMgr = function(){
	
	var floorMgrObj = this;
    this.id = null;
    this.desc = null;
    this.imgSrc = null;
    this.floorMap = new Array();
    this.occMap = new Array();
    this.featMap = new Array();
    this.doctorMap = new Array();
    this.roomMap = new Array();
    this.occDocMap = new Array();
    this.uniqueDocArr = new Array();
    
    this.worker = new Worker("controller/workerFile.js");
    this.workerTransport = null;

    this.getObj4Bed = function(occArr, bed) {
    	
    	for (obj in occArr) {
    		if (occArr[obj].bed == bed) return occArr[obj];
    	}
    };
    
	this.populateFloorLayer = function(buildId) {

		console.log("populateFloorLayer called ...");

		// Floor Image
		imageObj = new Image();
		imageObj.onload = function() {
			var image = new Kinetic.Image({
				x : 0,
				y : 20,
				image : imageObj,
				width : 1164,
				height : 500,
				name:  this.desc
			});
			$( "#diagFloor" ).html(canvasMgr.floorArr[buildId].description);
            $( "#diagRoom" ).html('---');
            $( "#diagBed" ).html('---');
            $( "#diagNumBeds" ).html(canvasMgr.floorArr[buildId].num_beds);
            canvasMgr.floorLyr.removeChildren();
            canvasMgr.floorLyr.add(image);
			//canvasMgr.stage.draw();
		
		};
		imageObj.src = canvasMgr.floorArr[buildId].img_src;
		//canvasMgr.stage.draw();

	};
	
 	this.getAndPaintFloorOccupancyInfo = function() {
 		
 		// check the 'bedByDate' array for the given date ...
 		if (canvasMgr.floorArr[canvasMgr.currFloor].bedByDate[canvasMgr.dt] == undefined) {
 			floorMgrObj.getFloorOcc4DateList();
 		}
 		floorMgrObj.createOccLayerMapInfo();
 	};
    
	
	// get occupancy for specific date and load array...
	this.getFloorOcc4DateList = function() {
		
		console.log('SRVR_call -> ' + "getFloorOcc4DateList called ... with param floorMgrObj.id =" + floorMgrObj.id);
		var buildId = canvasMgr.currFloor;
		var dt = canvasMgr.dt;
		
		var method = 'getFloorOcc4DateList';
        try {
        	
        	var params = {
                    buildId: buildId,
                    dt: dt,
                    currVis: canvasMgr.currVis
                };
            
            $.ajax({
                url: "FloorOccupancy",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	floorMgrObj.getFloorOcc4DateListElab(transport);
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
	
	this.getFloorOcc4DateListElab = function(transport) {
		
		var method="getFloorOcc4DateListElab";
		console.log(method + " called ...");
		
		var buildId = canvasMgr.currFloor;
		//var dt = canvasMgr.dt;
		var dt = transport.date;
		
		if (transport.error == undefined) {
			
			canvasMgr.floorArr[buildId].bedByDate[dt] = transport.ret2cli.occByBedMp;
			
			if (!$.isEmptyObject(transport.ret2cli.cleanByLocMp)) {
				canvasMgr.floorArr[buildId].cleanByDate[dt] = transport.ret2cli.cleanByLocMp;
			}
			
			canvasMgr.daysLoaded++;
			canvasMgr.colorDay(dt.substring(6));
			
		} else {
			var err = transport.error; 
			if (err.errorCode == 1) {
				canvasMgr.showError(transport.error.errorDesc);
			    
			} else {
				canvasMgr.showError(" in method -> " + method);
			};
			
		};
	};
	
	// WebWorker code (START)
	// worker listener method
	this.worker.addEventListener('message', function(e) {

		if (e.data.error != undefined) {
			floorMgrObj.workerTransport.error = e.data.error;
			console.log('e.data.error -> ' + e.data.error);
		} else {
		
			floorMgrObj.workerTransport = {ret2cli: {occByBedMp: undefined, cleanByLocMp: undefined}};
			if (e.data.ret2cli.occByBedMp != undefined) {
				floorMgrObj.workerTransport.ret2cli.occByBedMp = e.data.ret2cli.occByBedMp;
			} 
			if (e.data.ret2cli.cleanByLocMp != undefined) {
				floorMgrObj.workerTransport.ret2cli.cleanByLocMp = e.data.ret2cli.cleanByLocMp;
			} 
			if (e.data.date != undefined) {
				floorMgrObj.workerTransport.date = e.data.date;
			} 
			
			console.log("captured result ... see canvasMgr.floorMgr.workerTransport. Last day worked -> " + canvasMgr.lastWorkerDay);
			floorMgrObj.getFloorOcc4DateListElab(canvasMgr.floorMgr.workerTransport);
			canvasMgr.lastWorkerDay++;
			floorMgrObj.workerManager();
		}	
		
	}, true);
	
	this.workerManager = function() {
		
		if (canvasMgr.enableWorker) {
		
			if (canvasMgr.daysArr.length > 0) {
				
				var workerDay = canvasMgr.currYear+canvasMgr.currMonth+canvasMgr.daysArr.pop();
				if (canvasMgr.floorArr[canvasMgr.currFloor].bedByDate[workerDay] == undefined) { 	
					
					var reqObj = {'buildId': canvasMgr.currFloor, 'dt': workerDay};
					floorMgrObj.worker.postMessage(reqObj);
					
				} else {
					
					console.log("Already worked -> " + canvasMgr.lastWorkerDay);
					canvasMgr.colorDay(canvasMgr.lastWorkerDay);
					canvasMgr.lastWorkerDay++;
					floorMgrObj.workerManager();
				}
				
			}
			/*
			if (canvasMgr.daysLoaded < canvasMgr.lastDayOfMonth) {
			
				var day = (canvasMgr.lastWorkerDay.toString().length == 1) ? canvasMgr.lastWorkerDay="0"+canvasMgr.lastWorkerDay : canvasMgr.lastWorkerDay;
				//var day = Math.floor(Math.random() * (canvasMgr.lastDayOfMonth - 1 + 1)) + 1;
				day = (day.toString().length == 1) ? "0"+day : day;
				console.log("Random day -> " + day);
				var workerDay = canvasMgr.currYear+canvasMgr.currMonth+day;
				
				if (canvasMgr.floorArr[canvasMgr.currFloor].bedByDate[workerDay] == undefined) { 	
				
					var reqObj = {'buildId': canvasMgr.currFloor, 'dt': workerDay};
					floorMgrObj.worker.postMessage(reqObj);
					
				} else {
					
					console.log("Already worked -> " + canvasMgr.lastWorkerDay);
					canvasMgr.lastWorkerDay++;
					floorMgrObj.workerManager();
				}
			} else {
				//floorMgrObj.worker.terminate();
			}
			*/
		}
	};
	// WebWorker code (END)
	
    this.getVacantBedRange = function(buildId, dt, bed_num) {
		
		console.log('SRVR_call -> ' +  "getVacantBedRange called ...");
		
		var method = 'getVacantBedRange';
        try {
        	
        	var params = {
                    buildId: buildId,
                    dt: dt,
                    bed_num: bed_num,
                    action: "vacantBedRange"
                };
            
            $.ajax({
                url: "FloorOccupancy",
                dataType: "json",
                timeout: 10000,
                type: 'POST',
                async: false,
                data: params,
                context: document.body,
                success: function(transport){
                	floorMgrObj.getVacantBedRangeElab(buildId, dt, bed_num, transport);
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


    this.getVacantBedRangeElab = function(buildId, dt, room, transport) {
		
		var method="getVacantBedRangeElab";
		console.log(method + " called ...");
		
		if (transport.error == undefined) {
			
			//ret = transport.ret2cli;
			canvasMgr.floorArr[buildId].bedByDate[dt][room].dal = transport.ret2cli.data_dal;
			canvasMgr.floorArr[buildId].bedByDate[dt][room].al = transport.ret2cli.data_al;
			
		} else {
			var err = transport.error; 
			if (err.errorCode == 1) {
				canvasMgr.showError(transport.error.errorDesc);
			    
			} else {
				canvasMgr.showError(" in method -> " + method);
			};
			
		};
	};
	
	this.getFeatureInfo = function(buildId) {
		
		canvasMgr.featureLyr.removeChildren();
		
		var i=1;
		//for (f in floorMgrObj.featMap) {( function() {
	    for (f in canvasMgr.floorArr[buildId].featureArr) {( function() {	
			    //i++;
				var fObj = canvasMgr.floorArr[buildId].featureArr[f];
				var obj;
				var rom = fObj.room;
				var romDesc = rom+" ["+ fObj.desc + "]";
				var featType = fObj.type;
				console.log("featType -> " + featType);
				
				// I;Infermeria,A;Studio Medico,S;Soggiorno Cucinino,T;Vano Tecnico,B;Bagno"
				switch (featType) {
				  case "I": obj = canvasMgr.featImgI;
					  break;
				  case "A": obj = canvasMgr.featImgA;
					  break;
				  case "S": obj = canvasMgr.featImgS;
					  break;
				  case "T": obj = canvasMgr.featImgT;
				      break;
				  case "D": obj = canvasMgr.featImgD;
			      	  break;    
				  default: obj = canvasMgr.featImgB;
				};
				
				var xVal = fObj.x_val*i;
				var yVal = fObj.y_val*i;

				var image = new Kinetic.Image({
					x : xVal,
					y : yVal,
					image : obj,
					width : 20,
					height : 20,
					featType : featType,
					room : rom,
					draggable: canvasMgr.maintMode
				});
				
				image.on("dragstart", function() {
	            	document.body.style.cursor = "pointer";
	            });
	            image.on("dragmove", function() {
	                document.body.style.cursor = "pointer";
	            });
	            image.on("mouseover", function(){
	            	document.body.style.cursor = "pointer";
	            	
	            //});
	            //image.on("click", function(){
	            	var tooltip = new Kinetic.Label({
	                    x: xVal,
	                    y: yVal,
	                    opacity: 0.75,
	                    id: "tt01"
	                  });
	            	tooltip.add(new Kinetic.Tag({
	                    fill: 'black',
	                    pointerDirection: 'down',
	                    pointerWidth: 10,
	                    pointerHeight: 10,
	                    lineJoin: 'round',
	                    shadowColor: 'black',
	                    shadowBlur: 10,
	                    shadowOffset: {x:10,y:20},
	                    shadowOpacity: 0.5
	                  }));
	                tooltip.add(new Kinetic.Text({
	                    text: romDesc,
	                    fontFamily: 'Calibri',
	                    fontSize: 18,
	                    padding: 5,
	                    fill: 'white'
	                  }));
	                canvasMgr.featureLyr.add(tooltip);
	                canvasMgr.featureLyr.draw();
	            });
	            image.on("mouseout", function() {
	            	
	            	canvasMgr.featureLyr.find('#tt01').destroy();
	            	canvasMgr.featureLyr.draw();
	                document.body.style.cursor = "default";
	                $( "#diagRoom" ).html('---');
	            });
	            image.on("mousemove", function(){
	                $( "#diagRoom" ).html(romDesc);
	            });
				
	            canvasMgr.featureLyr.add(image);
			}());
		    //canvasMgr.floorLyr.draw();
		    //canvasMgr.stage.draw();
		};

	};
	
	this.createOccLayer = function(stage, layer, day) {

		console.log("createOccLayer() called");
		layer.removeChildren();
		
		if (!canvasMgr.maintMode) {
			// Doctor info
			//floorMgrObj.getDoctorOccupancyInfo(stage, layer, day, floorMgrObj.paintDoctorOccupancyInfo);
			floorMgrObj.createOccLayerMapInfo(stage, layer, day);
		} else {
			if (canvasMgr.maintModeType == "letti") {
				floorMgrObj.createOccLayerMaintInfo(stage, layer);
			} else { // for sure canvasMgr.maintModeType == "stanze" 
				canvasMgr.maintMgrRooms.initTst(this.id);
				// do nothing
			}	
		}
		
	};
	
	this.createOccLayerMaintInfo = function(stage, layer) {
		
		console.log("createOccLayerMaintInfo called ...");
		
		var fMap = floorMgrObj.floorMap;
		
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
	
	this.createOccLayerMapInfo = function() {
		
		
		var buildId = canvasMgr.currFloor;
		var dt = canvasMgr.dt;
		
		canvasMgr.occLyr.removeChildren();

	    for (o in canvasMgr.floorArr[buildId].bedByDate[dt]) {( function() {	
			
	    	 //OccupationalData: bed, name, status, gender, altro
	    	 var objA = canvasMgr.floorArr[buildId].bedByDate[dt][o];
	    	 	    		 
			 if (objA != undefined) {
			
		    	//StaticData: bed_num, room_num, x_val, y_val	 
		    	var objB = canvasMgr.floorArr[buildId].bedArr[objA.bed_num]; 
		    	
				var rom = objB.room_num;
				var bed = objB.bed_num; 
				var conf = "No";
				//var obj;
				var zoomFact = 1;
				
				if (objA.status != 3) { //Letto dissabilitato do nothing
				
						if (objA.status == 0) {
							obj = canvasMgr.xImgLib;
							bed = bed + " (libero)";
						} else {
							if (objA.status == 1) {
								bed = bed + " (prenotato)";
								if (objA.sesso == "M") {
									obj = canvasMgr.mImgPre;
								} else {
									obj = canvasMgr.fImgPre;
								};
							} else {
								
								if (objA.altro != undefined && objA.altro.split("_")[0] == "T") {
									conf="Si (" + objA.altro.split("_")[2] + ")";
								}
								
								bed = bed + " (occupato)";
								if (objA.sesso == "M") {
									if (conf == "No") {
									obj = canvasMgr.mImgOcc;
								} else {
									    obj = canvasMgr.mImgOccConf;
									}
								} else {
									if (conf == "No") {
									obj = canvasMgr.fImgOcc; 
									} else {
										obj = canvasMgr.fImgOccConf;
									}
									 
								};					
								if (objA.altro != undefined) {
								    var spl=objA.altro.split("_");
								    if (spl[0] == "T") {
								    	
								    } 
								}  
							} 	
						};
					    
					    var xVal = objB.x_val;
					    var yVal = objB.y_val;
						var image = new Kinetic.Image({
								x : xVal*zoomFact,
								y : yVal*zoomFact,
								image : obj,
								width : 20,
								height : 20,
								draggable: false,
								bed: objA.bed,
								codStanza: objB.room_num,
								building: buildId,
								room: objB.room_num
						});
						
			            image.on("dragstart", function() {
			            	document.body.style.cursor = "pointer";
			            });
			            image.on("dragmove", function() {
			                document.body.style.cursor = "pointer";
			            });
			            image.on("mouseover", function(){
			            	document.body.style.cursor = "pointer";
			            });
			            image.on("mouseout", function() {
			                document.body.style.cursor = "default";              
			                $( "#diagRoom" ).html('---');
			                $( "#diagBed" ).html('---');
			                $( "#dimConf" ).html('---');
			            });
						
			            image.on("mousemove", function(){
			            	
			            	var vals="";
			            	var bed_num = objB.bed_num;
			            	if (objA.status == 0) {
			            		if (canvasMgr.floorArr[buildId].bedByDate[dt][bed_num].dal == undefined) {
			            			floorMgrObj.getVacantBedRange(buildId, dt, bed_num);
			            		}
			            		vals = " dal: " + canvasMgr.floorArr[buildId].bedByDate[dt][bed_num].dal + " al: " + canvasMgr.floorArr[buildId].bedByDate[dt][bed_num].al;
			            	}
			            	
			                $( "#diagRoom" ).html(rom);
			                //$( "#diagBed" ).html(bed);
			                $( "#diagBed" ).html(bed + floorMgrObj.getInitials(objA.nome) + vals);
			                $( "#dimConf" ).html(conf);
			            });
			
			            canvasMgr.occLyr.add(image);
					  } else {
						  //Letto dissabilitato do nothing
						  //console.log(" WARN: objB is undefined for -> " + objA.status ) ;
					  }  
			 } // do nothing 
			
		    }());
		}
		//canvasMgr.occLyr.draw();
		//canvasMgr.stage.draw();
		
	};
	
	// helper function to get 3 intials from Name and Lastname ...
	this.getInitials = function(name) {
		
		var ret = "";		
		if (name != undefined && name.length > 0) {
			var nameArray = name.split(" ");
			var i=1;
			for (o in nameArray) {
				if (i==3) break;
				ret = ret + nameArray[o].substr(0,3) + " ";
				i++;
			};
			if (ret.length > 0) {
				ret = "(" + $.trim(ret) + ")";
			}
		}
		return ret;
	};
	
	this.rgb2hex = function(rgb) {
	    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
	    function hex(x) {
	        return ("0" + parseInt(x).toString(16)).slice(-2);
	    }
	    return hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
	    //  return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
	};
};