doctorsMgr = function(){
	
	var doctorsMgrObj = this;
    this.id = null;

	
	// get occupancy for specific date and load array...
	this.getDoctorsOcc4DateList = function(stage, layer, dt, _callback) {
		
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
	
	this.getDoctorsOcc4DateListElab = function(transport, stage, layer, _callback) {
		
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
};