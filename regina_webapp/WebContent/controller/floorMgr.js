floorMgr = function(){
	
	var floorMgrObj = this;
    this.id = null;
    this.desc = null;
    this.imgSrc = null;
    this.floorMap = new Array();
    this.occMap = new Array();

    this.getObj4Bed = function(occArr, bed) {
    	
    	for (obj in occArr) {
    		if (occArr[obj].bed == bed) return occArr[obj];
    	}
    };
};