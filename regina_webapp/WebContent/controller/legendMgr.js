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
};