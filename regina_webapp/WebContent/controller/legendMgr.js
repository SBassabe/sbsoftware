legendMgr = function(){
	
	var currObj = this;
	
	//Docs
	this.docColors = new Array();
	this.docColors[16173]={color:"green",name:"DR.SSA Dorigoni Sabina"};
	this.docColors[16169]={color:"cyan",name:"DR. Mininno Raffaele"};
	this.docColors[16170]={color:"yellow",name:"DR.SSA Dalbosco Barbara"};
	this.docColors[16168]={color:"orange",name:"DR.SSA Patton Laura"};
	this.docColors[16172]={color:"red",name:"DR.SSA Tonet Silvana"};
	this.docColors[16174]={color:"blue",name:"DR.SSA Guella Veronica"};
	this.docColors[17243]={color:"firebrick",name:"DR.Perazzolli Gabriele"};
	
	//support functions
	this.populateStage = function() {
		
		 console.log("populateStage called ...");
		 var i=0;
		 for (var o in this.docColors) {
			 var simpleText = new Kinetic.Text({
			        x: 940+22,
			        y: (i*14)+3,
			        text: this.docColors[o].name,
			        fontSize: 18,
			        fontFamily: 'Calibri',
			        fill: 'black'
			 });
			 
			 var rect = new Kinetic.Rect({
			        x: 940+0,
			        y: (i*14)+7,
			        width: 20,
			        height: 12,
			        fill: this.docColors[o].color,
			        stroke: 'black',
			        strokeWidth: 0.1,
			        opacity: 0.6
			 });
			 canvasMgr.toolTipLyr.add(rect);
			 canvasMgr.toolTipLyr.add(simpleText);
			 i++;
		 }
		 canvasMgr.stage.draw();
	};
	
	this.populateToolTipLyrWithUniqueDocs = function() {
		
		 console.log("populateToolTipLyrWithUniqueDocs called ...");
		 canvasMgr.toolTipLyr.removeChildren();
		 var floorId = canvasMgr.currFloor;
		 var i=0;
		 
		 for (var o in canvasMgr.floorArr[floorId].uniqueDocArr) {
			 
			 var docDesc=currObj.docColors[canvasMgr.floorArr[floorId].uniqueDocArr[o]];
			 if (docDesc != undefined) {
			 
				 var simpleText = new Kinetic.Text({
				        x: 940+22,
				        y: (i*14)+3,
				        text: docDesc.name,
				        fontSize: 18,
				        fontFamily: 'Calibri',
				        fill: 'black'
				 });
				 
				 var rect = new Kinetic.Rect({
				        x: 940+0,
				        y: (i*14)+7,
				        width: 20,
				        height: 12,
				        fill: docDesc.color,
				        stroke: 'black',
				        strokeWidth: 0.1,
				        opacity: 0.6
				 });
			 
				 canvasMgr.toolTipLyr.add(rect);
				 canvasMgr.toolTipLyr.add(simpleText);
				 i++;
			 }
		 }
		 canvasMgr.toolTipLyr.batchDraw();
	};
	
	//Cleaning Ovrlays
	// http://www.rapidtables.com/web/color/RGB_Color.htm
	this.cleanColor = new Array();
	this.cleanColor[0]={color:"red", rgba:'rgba(255,0,0,0.5)', desc: "Pulizia non richiesta"};
	this.cleanColor[0.25]={color:"yellow", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia parziale"};
	this.cleanColor[0.50]={color:"yellow", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia parziale"};
	this.cleanColor[0.75]={color:"yellow", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia parziale"};
	this.cleanColor[1]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da pulire"};
	this.cleanColor[1.25]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da pulire"};
	this.cleanColor[1.50]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da pulire"};
	this.cleanColor[1.75]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da pulire"};
	this.cleanColor[2]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da pulire"};
	
	this.cleanColorLegend = new Array();
	this.cleanColorLegend[0]={color:"red", rgba:'rgba(255,0,0,0.5)', desc: "Pulizia Non Richiesta"};
	this.cleanColorLegend[1]={color:"yellow", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia Parziale"};
	this.cleanColorLegend[2]={color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Da Pulire"};
	
	this.populateToolTipWithCleaningLegend = function() {
		
		 console.log("populateToolTipWithCleaningLegend called ...");
		 canvasMgr.toolTipLyr.removeChildren();
		 var i=0;
		 
		 for (var o in this.cleanColorLegend) {
			 
			 var cColor=this.cleanColorLegend[o];
			 if (cColor != undefined) {
			 
				 var simpleText = new Kinetic.Text({
				        x: 940+22,
				        y: (i*14)+3,
				        text: o + ' - ' + cColor.desc,
				        fontSize: 18,
				        fontFamily: 'Calibri',
				        fill: 'black'
				 });
				 
				 var rect = new Kinetic.Rect({
				        x: 940+0,
				        y: (i*14)+7,
				        width: 20,
				        height: 12,
				        fill: cColor.rgba,
				        stroke: 'black',
				        strokeWidth: 0.1,
				        opacity: 0.6
				 });
			 
				 canvasMgr.toolTipLyr.add(rect);
				 canvasMgr.toolTipLyr.add(simpleText);
				 i++;
			 }
		 }
		 canvasMgr.toolTipLyr.batchDraw();
	};
};