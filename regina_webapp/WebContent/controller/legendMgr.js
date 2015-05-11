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
	this.docColors[17542]={color:"aqua",name:"DR. Cucino Alberto"};	
	this.docColors[16867]={color:"crimson",name:"DR.SSA Galletti Annacristina"};
	this.docColors[16205]={color:"deeppink",name:"Stanze Stand-By"};

	/*  16168	 DR.SSA PATTON LAURA 
		16169	 DR. MININNO RAFFAELE
		16170	 DR.SSA DALBOSCO BARBARA 
		16172	 DR.SSA TONET SILVANA
		16173	 DR.SSA DORIGONI SABINA 
		16174	 DR.SSA GUELLA VERONICA
		16205	 STANZE STAND BY
		16867	GALLETTI ANNACRISTINA
		17243	 DR. PERAZZOLLI GABRIELE
		17542	 DR. CUCINO ALBERTO
     */
	
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
	this.cleanColor[0]={opacity:0.4, color:"red", rgba:'rgba(255,0,0,0.5)', desc: "Pulizia non richiesta"};
	this.cleanColor[0.50]={opacity:0.4, color:"yellow", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia parziale"};
	this.cleanColor[1]={opacity:0.5, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia completa"};
	this.cleanColor[2]={opacity:0.6, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia 2 volte al di"};
	this.cleanColor[3]={opacity:0.8, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia 3 volte al di"};
	
	this.cleanColorLegend = new Array();
	this.cleanColorLegend[0]={num:0, opacity:0.4, color:"red", rgba:'rgba(255,0,0,0.5)', desc: "Pulizia non richiesta"};
	this.cleanColorLegend[1]={num:0.5, opacity:0.4, color:"red", rgba:'rgba(255,255,0,0.5)', desc: "Pulizia parziale"};
	this.cleanColorLegend[2]={num:1, opacity:0.5, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia completa"};
	this.cleanColorLegend[3]={num:2, opacity:0.6, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia 2 volte al di"};
	this.cleanColorLegend[4]={num:3, opacity:0.8, color:"green", rgba:'rgba(0,255,0,0.5)', desc: "Pulizia 3 volte al di"};
	
	this.populateToolTipWithCleaningLegend = function() {
		
		 console.log("populateToolTipWithCleaningLegend called ...");
		 canvasMgr.toolTipLyr.removeChildren();
		 var i=0;
		 
		 for (var o in this.cleanColorLegend) {
			 
			 console.log('o' + o);
			 var cColor=this.cleanColorLegend[o];
			 if (cColor != undefined) {
			 
				 var simpleText = new Kinetic.Text({
				        x: 940+22,
				        y: (i*14)+3,
				        text: cColor.num + ' - ' + cColor.desc,
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
				        opacity: cColor.opacity
				 });
			 
				 canvasMgr.toolTipLyr.add(rect);
				 canvasMgr.toolTipLyr.add(simpleText);
				 i++;
			 }
		 }
		 canvasMgr.toolTipLyr.batchDraw();
	};
};