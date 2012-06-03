maintMgr = function() {
	
	var maintObj = this;
	this.polyArray = new Array(); 
	
	this.initTst = function(layer) {
		
        var cPoly1 = "73,192,73,160,340,23,500,109,499,139,342,93";
        var cPoly2 = "626,401,628,324,744,324,994,323,996,403,628,402";

        maintObj.polyArray.push(cPoly1);
        maintObj.polyArray.push(cPoly2);
     
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
		
		console.log("updateDotted");
		var i;
    	for (i=0; i<maintObj.polyArray.length; i++) {
	        var c = eval("layer.polyPnts" + i);
	        var polyLine = layer.get('#polyLine'+i)[0];
	        
	        if (c == undefined || polyLine == undefined) return;
	        
	        polyLine.setPoints([
	          c.start.attrs.x, c.start.attrs.y, 
	          c.control1.attrs.x, c.control1.attrs.y, 
	          c.control2.attrs.x, c.control2.attrs.y,
	          c.control3.attrs.x, c.control3.attrs.y,
	          c.control4.attrs.x, c.control4.attrs.y,
	          c.end.attrs.x, c.end.attrs.y
	        ]);
    	}
	};
	
	this.buildAnchor = function(layer, x, y) {
		
		console.log("buildAnchor");
		var anchor = new Kinetic.Circle({
	          x: parseInt(x),
	          y: parseInt(y),
	          radius: 3,
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
		  
		  for (o in maintObj.polyArray) {
			  
			var c = maintObj.polyArray[o].split(",");  
			var id = "polyLine"+i;
		        
			// crate polygon and add it to layer
			poly = new Kinetic.Polygon({
			        //points: c,
			        fill: "#00D2F0",
			        alpha: 0.2,
			        stroke: "#00D2F0",
			        strokeWidth: 0,
			        id: id
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
};