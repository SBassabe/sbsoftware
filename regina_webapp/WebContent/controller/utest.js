utest = function() {
	
	 var currUtstObj = this;
	 this.tmr="";
	 	 
	 this.changeDate = function(){
		 
		 console.log("into changeDate ...");
		 var month = parseInt($('#month').val());
		 var day = parseInt($('#day').val());
		 day+=1;
		 
		 if(month == 10) month=1;
		 if(day == 28) {
			 month+=1;
			 day=1;
		 }
		 
		 $('#month').val('0'+month);
		 $('#day').val(''+day);
		 $('#dayvalue').text(''+day);
		 canvasMgr.selectorsChanged();
		 
	 };
	 
	 this.startSmart = function() {
		 
		 tmr=setInterval(function() {currUtstObj.changeDate();}, 700);
		 
	 };
	 
};
