applicationMgr = function() {
	
	 var currObj = this;
	 this.jsFileName = "application.js";
	 this.canvasMgr = new canvasMgr();
	 	 
	 this.start = function(){
		 currObj.canvasMgr.init();
	 };
};