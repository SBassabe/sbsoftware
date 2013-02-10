utest = function() {
	
	 var currUtstObj = this;
	 this.tmr="";
	 	 
	 this.changeDate = function(){
		 
		 console.log("into changeDate ...");
		 var month = parseInt($('#month').val());
		 var day = $('#day').slider( "option", "value");
		 
		 day+=1;
		 
		 if(month == 10) month=1;
		 if(day == 28) {
			 month+=1;
			 day=1;
		 }
		 
		 $('#month').val('0'+month);
		 $('#day').slider( "option", "value", day);
		 $('#dayvalue').text(''+day);
		 canvasMgr.selectorsChanged();
		 
	 };
	 
	 this.startSmart = function() {
		 
		 tmr=setInterval(function() {currUtstObj.changeDate();}, 500);
		 
	 };
	 
	 this.stopSmart = function() {
		 
		 clearInterval(tmr);
		 
	 };
	 
};
