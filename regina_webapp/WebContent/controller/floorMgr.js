floorMgr = function(){
	
	var floorMgrObj = this;
    var id = null;
    var desc = null;
    var imgSrc = null;
    var floorMap = new Array();
    this.occMap = new Array();

    this.getObj4Bed = function(occArr, bed) {
    	
    	for (obj in occArr) {
    		if (occArr[obj].bed == bed) return occArr[obj];
    	}
    };
};