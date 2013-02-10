floorMgr = function(){
	
	var floorMgrObj = this;
    this.id = null;
    this.desc = null;
    this.imgSrc = null;
    this.floorMap = new Array();
    this.occMap = new Array();
    this.featMap = new Array();
    this.doctorMap = new Array();

    this.getObj4Bed = function(occArr, bed) {
    	
    	for (obj in occArr) {
    		if (occArr[obj].bed == bed) return occArr[obj];
    	}
    };
    
	this.populateFloorLayer = function(stage, flrLayer, occLayer, dt) {

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
			$( "#diagFloor" ).html(floorMgrObj.desc);
            $( "#diagRoom" ).html('---');
            $( "#diagBed" ).html('---');
            $( "#diagNumBeds" ).html(floorMgrObj.floorMap.length);
            flrLayer.removeChildren();
            flrLayer.add(image);
			stage.draw();
			
			occLayer.removeChildren();
			if (!canvasMgr.maintMode && floorMgrObj.occMap[dt] == undefined ) {
					floorMgrObj.getFloorOcc4DateList(stage, occLayer, dt, floorMgrObj.createOccLayer);
				} else {
				// Maint or Real logic in method
					floorMgrObj.createOccLayer(stage, occLayer, dt);
				}

			// Doctor info
			floorMgrObj.getDoctorInfo(stage, canvasMgr.floorLyr);
			// Floor features
			floorMgrObj.getFeatureInfo(stage, canvasMgr.floorLyr);
		
		};
		imageObj.src = this.imgSrc;
		stage.draw();

	};
	
	// get occupancy for specific date and load array...
	this.getFloorOcc4DateList = function(stage, layer, dt, _callback) {
		
		console.log("getFloorOcc4DateList called ...");
		console.log("floorMgrObj.id =" + floorMgrObj.id);
		
		var method = 'getFloorOcc4DateList';
        try {
        	
        	var params = {
                    buildId: this.id,
                    dt: dt
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
                	floorMgrObj.getFloorOcc4DateListElab(transport, stage, layer, _callback);
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
	
	this.getFloorOcc4DateListElab = function(transport, stage, layer, _callback) {
		
		console.log("getFloorOcc4DateListElab called ...");
		console.log("floorMgrObj.id = " + floorMgrObj.id);
		var method="getFloorOcc4DateListElab";
		
		if (transport.error == undefined) {
			
			ret = transport.ret2cli;
			floorMgrObj.occMap[ret.dt] = ret.occMap;
			_callback(stage, layer, ret.dt);
			//floorMgrObj.createOccLayer(stage, layer, ret.dt);
			
		} else {
			var err = transport.error; 
			if (err.errorCode == 1) {
				canvasMgr.showError(transport.error.errorDesc);
			    
			} else {
				canvasMgr.showError(" in method -> " + method);
			};
			
		};
	};
	
	this.getDoctorInfo = function(stage, layer) {
		
		console.log("getDoctorInfo called ...");
		
		if (canvasMgr.maintMode) {
			canvasMgr.maintMgr.clearDoctorInfo(layer);
			canvasMgr.maintMgr.initTst(layer, floorMgrObj.doctorMap);
		} else {
		
			// Doctor info
			for (f in floorMgrObj.doctorMap) {( function() {
				
				var dObj = floorMgrObj.doctorMap[f];
				//var polyPnts = "[" + dObj.polyPoints + "]";
				var polyPnts = dObj.polyPoints.split(",");
	 			
				var poly2 = new Kinetic.Polygon({
				        points: polyPnts,
				        fill: dObj.color,
				        //alpha: 0.2,
				        opacity: 0.6,
				        //stroke: dObj.color,
				        strokeWidth: 0,
				        id: "p_" + dObj.docId,
				        docName: dObj.docName,
				        rooms: dObj.rooms
			    });
			        
				poly2.on("mouseout", function() {;
	                $( "#diagRoom" ).html('---');
	            });
				poly2.on("mousemove", function(){
	                $( "#diagRoom" ).html("(Doc: " + dObj.docName + ") " + dObj.rooms);
	            });
			        
				layer.add(poly2);
				
				}());
			};
			layer.draw();
			stage.draw();
		}
	};
	
	this.getFeatureInfo = function(stage, layer) {
		
		var i=1;
		for (f in floorMgrObj.featMap) {( function() {
			    //i++;
				var fObj = floorMgrObj.featMap[f];
				var obj;
				var rom = fObj.room;
				var romDesc = rom+"["+ fObj.featDesc + "]";
				var featType = fObj.featType;
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
				
				xVal = fObj.xVal*i;
				yVal = fObj.yVal*i;

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
	            });
	            image.on("mouseout", function() {
	                document.body.style.cursor = "default";
	                $( "#diagRoom" ).html('---');
	            });
	            image.on("mousemove", function(){
	                $( "#diagRoom" ).html(romDesc);
	            });
				
				layer.add(image);
			}());
				layer.draw();
				stage.draw();
		};

	};
	
	this.createOccLayer = function(stage, layer, day) {

		console.log("createOccLayer() called");
		layer.removeChildren();
		
		if (!canvasMgr.maintMode) {
			floorMgrObj.createOccLayerMapInfo(stage, layer, day);
		} else {
			floorMgrObj.createOccLayerMaintInfo(stage, layer);
		}
		
	};
	
	this.createOccLayerMaintInfo = function(stage, layer) {
		
		console.log("createOccLayerMaintInfo called ...");
		
		var fMap = floorMgrObj.floorMap;
		
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
	
	this.createOccLayerMapInfo = function(stage, layer, day) {
		
		console.log("createOccLayerMapInfo called ...");
		
		var fMap = floorMgrObj.floorMap;
		var oMap = floorMgrObj.occMap[day];
		
		for (mObj in fMap) {( function() {
		//for (mObj in fMap) {
		    
			var cObj = floorMgrObj.getObj4Bed(oMap, fMap[mObj].bed);
			  if (cObj != undefined) {
				var rom = fMap[mObj].room;
				var bed = cObj.bed; 
				var conf = "No";
				var obj;
				var zoomFact = 1;
				
				if (cObj.status == 0) {
					obj = canvasMgr.xImgLib;
					bed = bed + " (libero)";
				} else {
					if (cObj.status == 1) {
						bed = bed + " (prenotato)";
						if (cObj.gender == "M") {
							obj = canvasMgr.mImgPre;
						} else {
							obj = canvasMgr.fImgPre;
						};
					} else {
						
						if (cObj.altro != undefined && cObj.altro.split("_")[0] == "T") {
							conf="Si (" + cObj.altro.split("_")[2] + ")";
						}
						
						bed = bed + " (occupato)";
						if (cObj.gender == "M") {
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
						if (cObj.altro != undefined) {
						    var spl=cObj.altro.split("_");
						    if (spl[0] == "T") {
						    	
						    } 
						}  
					} 	
				};
			    
			    var xVal = fMap[mObj].xVal;
			    var yVal = fMap[mObj].yVal;
				var image = new Kinetic.Image({
						x : xVal*zoomFact,
						y : yVal*zoomFact,
						image : obj,
						width : 20,
						height : 20,
						draggable: false,
						bed: cObj.bed,
						codStanza: fMap[mObj].codStanza,
						building: fMap[mObj].building,
						room: fMap[mObj].room
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
	                $( "#diagRoom" ).html(rom);
	                //$( "#diagBed" ).html(bed);
	                $( "#diagBed" ).html(bed + floorMgrObj.getInitials(cObj.name));
	                $( "#dimConf" ).html(conf);
	            });
	
	            layer.add(image);
			  } else {console.log(" WARN: cObj is undefined for mObj -> " + mObj ) ;}  
		}());
	  }
	  layer.draw();
	  stage.draw();
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
	
	
	// Doctor Table
	// Actions......
	this.createDoctorTable = function() {
		
		var param = {
			doctorMap: floorMgrObj.doctorMap
		};
		
		// Create the table
		try {
			var myejs = new EJS({
				url : './view/doctorsTable.ejs'
			});
			html = myejs.render(param);
		} catch (e) {
			if (e.description)
				e = e.description;
			console.log('ex : ' + e);
		}
		$('#doctorTable').html(html);
	
		// Build color pickers
		console.log("into colorPicker creation ...");
		$('.colorPal').ColorPicker({
			color: '#0000ff',
			onSubmit: function(hsb, hex, rgb, el) {
				floorMgrObj.updateDoctorTableObj(el,hex);
			},
			onShow: function (colpkr) {
				$(colpkr).fadeIn(500);
				return false;
			},
			onHide: function (colpkr) {
				$(colpkr).fadeOut(500);
				console.log('exiting');
				return false;
			},
		});
		
	};
	
	this.updateDoctorTableObj = function(el,hex) {
		
		console.log("updateDoctorTableObj called ... with el.id -> " + el.id);
		var id = el.id;
		
		el.style.backgroundColor='#'+hex;
		var polyObj = canvasMgr.floorLyr.get("#"+id)[0]; // Caution, this gets every 'p_doc0' in the layer. consider 'p_A0_doc0' as key
		if (polyObj != undefined) {
			polyObj.setFill(hex);
			polyObj.setStroke(hex);
			canvasMgr.stage.draw();
		};
	};
	
	this.doDocNew = function() {
		
		console.log("doNew");

		var docName = $('#newName').val();
		var romRang = $('#newRooms').val();
		//var color = $('#colorP').css('background-color');
		
		/*
				<option style="background-color: orange;">DR.SSA DORIGONI</option>
				<option style="background-color: cyan;">DR MININNO</option>
				<option style="background-color: yellow;">DR.SSA DALBOSCO</option>
				<option style="background-color: red;">DR.SSA TONET</option>
				<option style="background-color: green;">DR.SSA PATTON</option> 
		 */
		
		if (docName == "DR.SSA DORIGONI") color="orange";
		if (docName == "DR MININNO") color="cyan";
		if (docName == "DR.SSA DALBOSCO") color="yellow";
		if (docName == "DR.SSA TONET") color="red";
		if (docName == "DR.SSA PATTON") color="green";
		
		// Just in case
		romRang = romRang.replace(',',' ');
		romRang = romRang.replace(';',' ');
		
		console.log("docName -> " + docName);
		console.log("romRang -> " + romRang);
		console.log("color -> " + color);
		
		//console.log("colorHx -> " + floorMgrObj.rgb2hex(color));
		
		// Standard polyPoints:
		// 452;389;439;337;460;283;515;283;537;341;521;388
		
		var sz = floorMgrObj.doctorMap.length+1; 
		
		var docObj = {
			building: canvasMgr.currFloor,
			//color: floorMgrObj.rgb2hex(color),
			color: color,
			docId: canvasMgr.currFloor + "Doc" + sz,
			docName: docName,
			polyPoints: "452;389;439;337;460;283;515;283;537;341;521;388",
			//polyPoints: "452,389,439,337,460,283,515,283,537,341,521,388",
			rooms: romRang
		}; 
		
		floorMgrObj.doctorMap.push(docObj);
		
		// DOCID; COLOR; DOCNAME; ROOMS; POLYPOINTS
		// polyPoints: "452;389;439;337;460;283;515;283;537;341;521;388",
		var docObj4Call = docObj.docId + ";" + docObj.color + ";" + docObj.docName + ";" + docObj.rooms + ";" + docObj.polyPoints;
		canvasMgr.maintMgr.callMgmtSrvltDoc(docObj4Call, "", "newDoc");
		canvasMgr.checkMaintRadio("off");
		canvasMgr.checkMaintRadio("on");
		
		$('#doctorTable').empty();
		floorMgrObj.createDoctorTable();
		
	};

	this.doDocDelete = function(id) {
		
		console.log("doDocDelete called with id -> " + id);
		canvasMgr.maintMgr.callMgmtSrvltDoc("placemark", id, "delDoc");
		canvasMgr.checkMaintRadio("off");
		canvasMgr.checkMaintRadio("on");
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